package microservice.dungeon.game.aggregates.game.domain

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.round.domain.Round
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
    private var totalRoundTimespanInMS: Double,

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
        totalRoundTimespanInMS = 60000.00,
        relativeCommandInputTimespanInPercent = 75,
        participatingPlayers = mutableSetOf(),
        rounds = mutableSetOf()
    )

    @Transient
    private val logger = KotlinLogging.logger {}

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
        val currentRound: Round = getCurrentRound()!!
        currentRound.endRound()
        logger.debug("Ended previous Round.")
        logger.trace(currentRound.toString())

        val nextRound = Round(game = this, roundNumber = currentRound.getRoundNumber() + 1)
        rounds.add(nextRound)
        logger.debug("Added next Round.")
        logger.trace(nextRound.toString())
    }

    fun endGame() {
        gameStatus = GameStatus.GAME_FINISHED
        logger.debug("GameStatus set to $gameStatus")
    }

    fun getGameId(): UUID = gameId

    fun getGameStatus(): GameStatus = gameStatus

    fun getCurrentRound(): Round? = rounds.fold(null) { acc: Round?, e: Round ->
     if (acc != null && acc.getRoundNumber() > e.getRoundNumber()) acc else e
    }

    fun getRound(roundNumber: Int): Round = rounds.first { round ->
        round.getRoundNumber() == roundNumber
    }
}