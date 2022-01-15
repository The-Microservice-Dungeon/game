package microservice.dungeon.game.aggregates.game.domain

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import mu.KotlinLogging
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "GAMES")
class Game constructor (
    @Id
    @Type(type="uuid-char")
    @Column(name = "GAME_ID")
    private var gameId: UUID,

    @Column(name = "GAME_STATUS")
    private var gameStatus: GameStatus,

    @Column(name = "MAX_PLAYERS")
    private var maxPlayers: Int,

    @Column(name = "MAX_ROUNDS")
    private var maxRounds: Int,

    @Column(name = "TOTAL_ROUND_TIMESPAN")
    private var totalRoundTimespanInMS: Long,

    @Column(name = "RELATIVE_COMMAND_INPUT_TIMESPAN")
    private var relativeCommandInputTimespanInPercent: Int,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "GAME_PARTICIPATIONS",
        joinColumns = [JoinColumn(name = "GAME_ID")],
        inverseJoinColumns = [JoinColumn(name = "ROUND_ID")])
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

    override fun toString(): String {
        return "Game(gameId=$gameId, gameStatus=$gameStatus, maxPlayers=$maxPlayers, maxRounds=$maxRounds, totalRoundTimespanInMS=$totalRoundTimespanInMS, relativeCommandInputTimespanInPercent=$relativeCommandInputTimespanInPercent, participatingPlayers=$participatingPlayers, rounds=$rounds)"
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