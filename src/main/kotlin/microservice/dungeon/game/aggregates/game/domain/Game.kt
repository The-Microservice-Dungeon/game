package microservice.dungeon.game.aggregates.game.domain

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import mu.KotlinLogging
import org.hibernate.annotations.Type
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(
    name = "games",
    indexes = [
        Index(name = "game_gameStatus", columnList = "game_status")
    ]
)
class Game constructor (
    @Id
    @Type(type="uuid-char")
    @Column(name = "game_id")
    private var gameId: UUID,

    @Column(name = "game_status")
    private var gameStatus: GameStatus,

    @Column(name = "max_players")
    private var maxPlayers: Int,

    @Column(name = "max_rounds")
    private var maxRounds: Int,

    @Column(name = "total_round_timespan")
    private var totalRoundTimespanInMS: Long,

    @Column(name = "relative_command_input_timespan")
    private var relativeCommandInputTimespanInPercent: Int,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "game_participations",
        joinColumns = [JoinColumn(name = "game_id")],
        inverseJoinColumns = [JoinColumn(name = "round_id")])
    private var participatingPlayers: MutableSet<Player>,

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    private var rounds: MutableSet<Round>

) {
    constructor(maximumPlayers: Int, maximumRounds: Int): this(
        gameId = UUID.randomUUID(),
        gameStatus = GameStatus.CREATED,
        maxPlayers = maximumPlayers,
        maxRounds = maximumRounds,
        totalRoundTimespanInMS = 60000,
        relativeCommandInputTimespanInPercent = 75,
        participatingPlayers = mutableSetOf(),
        rounds = mutableSetOf()
    )

    companion object {
        private val logger = KotlinLogging.logger {}
    }


    fun startGame() {
        if (gameStatus != GameStatus.CREATED) {
            logger.warn("Failed to start Game, because Status is other then CREATED. {}", gameStatus)
            throw GameStateException("Game can not be started, because its status is other than CREATED. Status is $gameStatus.")
        }
        gameStatus = GameStatus.GAME_RUNNING
        logger.debug("Game-Status set to $gameStatus")

        val newRound = Round(game = this, roundNumber = 1)
        rounds.add(newRound)
        logger.debug("Round added to Game.")
        logger.trace(newRound.toString())
    }

    fun startNewRound() {
        if (gameStatus != GameStatus.GAME_RUNNING) {
            logger.warn("Failed to start a new round, because the Game-Status is other than RUNNING. {}", gameStatus)
            throw GameStateException("Game could not start a new round, because its status is other than RUNNING. Status is $gameStatus")
        }
        if (getCurrentRound() != null && getCurrentRound()!!.getRoundNumber() >= maxRounds) {
            logger.debug("Start new round aborted. Maximum number of rounds reached. [${getCurrentRound()!!.getRoundNumber()}/${maxRounds}]")
            throw GameStateException("Start new round aborted. Maximum number of rounds reached. [${getCurrentRound()!!.getRoundNumber()}/${maxRounds}]")
        }

        val currentRound: Round = getCurrentRound()!!
        if (currentRound.getRoundStatus() != RoundStatus.ROUND_ENDED) {
            currentRound.endRound()
            logger.debug("Ended previous Round.")
            logger.trace(currentRound.toString())
        }

        val nextRound = Round(game = this, roundNumber = currentRound.getRoundNumber() + 1)
        rounds.add(nextRound)
        logger.debug("Added next Round.")
        logger.trace(nextRound.toString())
    }

    fun endGame() {
        gameStatus = GameStatus.GAME_FINISHED
        logger.debug("GameStatus set to $gameStatus")
    }

    fun joinGame(player: Player) {
        if (gameStatus != GameStatus.CREATED) {
            logger.warn("Player failed to join the Game, because its status is other than CREATED. [gameStatus=$gameStatus, playerName=${player.getUserName()}]")
            throw GameStateException("Failed to join Game. Games may only be joined when status is CREATED. Current status is $gameStatus")
        }

        if (participatingPlayers.map { it.getPlayerId() }.contains(player.getPlayerId())) {
            logger.warn("Player failed to join the Game, because he is already participating. [playerName=${player.getUserName()}]")
            throw GameParticipationException("Failed to join Game. Player is already participating.")
        }

        if (participatingPlayers.size >= maxPlayers) {
            logger.warn("Player failed to join the Game, because its already full. [playerName=${player.getUserName()}, currentPlayers=${participatingPlayers.size}, maxPlayers=$maxPlayers]")
            throw GameParticipationException("Failed to join Game. Game is already full. [currentPlayers=${participatingPlayers.size}, maxPlayers=$maxPlayers]")
        }

        participatingPlayers.add(player)
        logger.debug("Added Player to list-of participating Players. [playerName=${player.getUserName()}]")
    }

    fun getGameId(): UUID = gameId

    fun getGameStatus(): GameStatus = gameStatus

    fun getMaxPlayers(): Int = maxPlayers

    fun getParticipatingPlayers(): List<Player> = participatingPlayers.toList()

    fun getNumberJoinedPlayers(): Int = participatingPlayers.size

    fun getMaxRounds(): Int = maxRounds

    fun getCurrentRound(): Round? = rounds.fold(null) { acc: Round?, e: Round ->
     if (acc != null && acc.getRoundNumber() > e.getRoundNumber()) acc else e
    }

    fun getRound(roundNumber: Int): Round = rounds.first { round ->
        round.getRoundNumber() == roundNumber
    }

    fun getTotalRoundTimespanInMS(): Long = totalRoundTimespanInMS

    fun setTotalRoundTimespanInMS(totalTime: Long) {
        totalRoundTimespanInMS = totalTime
    }

    fun getRelativeCommandInputTimespanInPercent(): Int = relativeCommandInputTimespanInPercent

    fun getTimeGameStartedTruncatedToSeconds(): LocalDateTime? {
        return if (gameStatus == GameStatus.CREATED) {
            null
        } else {
            getRound(1).getRoundStarted()
        }
    }

    fun getTimeSinceGameStartInSeconds(): Long? {
        return if (gameStatus == GameStatus.CREATED) {
            null
        } else {
            ChronoUnit.SECONDS.between(getTimeGameStartedTruncatedToSeconds()!!, LocalDateTime.now())
        }
    }

    fun isParticipating(player: Player): Boolean {
        return participatingPlayers.asIterable().map { it.getPlayerId() }.contains(player.getPlayerId())
    }

    fun changeMaximumNumberOfRounds(maxRounds: Int) {
        if (gameStatus == GameStatus.GAME_FINISHED) {
            logger.warn("Failed to change maximum number of rounds. Game is already finished.")
            throw GameStateException("Game is already finished.")
        }
        if (maxRounds < 1) {
            logger.warn("Failed to change maximum number of rounds. Maximum number of rounds may never be below 1. [requested=$maxRounds]")
            throw IllegalArgumentException("Maximum number of rounds may never below 1.")
        }
        if (getCurrentRound() != null && maxRounds <= getCurrentRound()!!.getRoundNumber()) {
            logger.warn("Failed to change maximum number of rounds. Maximum number of rounds may never be below the current round-number. [current=${getCurrentRound()!!.getRoundNumber()}, requested=$maxRounds]")
            throw IllegalArgumentException("Maximum number of rounds is below current round-number.")
        }
        this.maxRounds = maxRounds
        logger.debug("Changed maximum number of rounds to $maxRounds. [gameId=$gameId]")
    }

    fun changeRoundDuration(duration: Long) {
        if (gameStatus == GameStatus.GAME_FINISHED) {
            logger.warn("Failed to change game duration. Game is already closed. [gameStatus=$gameStatus]")
            throw GameStateException("Game is already finished.")
        }
        if (duration < 2000) {
            logger.warn("Failed to change game duration. Game duration may never be below 2 seconds. [durationInMillis=$duration]")
            throw IllegalArgumentException("Game duration may never be below 2 seconds. [durationInMillis=$duration]")
        }
        this.totalRoundTimespanInMS = duration
        logger.debug("Changed totalRoundTimespanInMS to $duration. [gameId=$gameId]")
    }

    override fun toString(): String {
        return "Game(gameId=$gameId, gameStatus='$gameStatus', maxPlayers=$maxPlayers, maxRounds=$maxRounds, totalRoundTimespanInMS=$totalRoundTimespanInMS, relativeCommandInputTimespanInPercent=$relativeCommandInputTimespanInPercent, participatingPlayers=${participatingPlayers.map{it.getUserName()}}, currentRoundNumber=${getCurrentRound()?.getRoundNumber()})"
    }

    fun isEqualByVal(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (gameId != other.gameId) return false
        if (gameStatus != other.gameStatus) return false
        if (maxPlayers != other.maxPlayers) return false
        if (maxRounds != other.maxRounds) return false
        if (totalRoundTimespanInMS != other.totalRoundTimespanInMS) return false
        if (relativeCommandInputTimespanInPercent != other.relativeCommandInputTimespanInPercent) return false
        if (!participatingPlayers.zip(other.participatingPlayers).fold(true) { acc, pair -> acc && pair.first.getPlayerId() == pair.second.getPlayerId() }) return false
        if (!rounds.zip(other.rounds).fold(true) { acc, pair -> acc && pair.first.getRoundId() == pair.second.getRoundId() }) return false
            return true
    }
}