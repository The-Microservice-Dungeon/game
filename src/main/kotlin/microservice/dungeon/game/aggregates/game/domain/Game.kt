package microservice.dungeon.game.aggregates.game.domain

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.round.domain.Round
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

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

    fun startGame() {
        gameStatus = GameStatus.GAME_RUNNING
        rounds.add(Round(game = this, roundNumber = 1))
    }

    fun startNewRound() {

    }

    fun endCommandInputPhase() {

    }

    fun endGame() {

    }


    fun getGameId(): UUID = gameId

    fun getGameStatus(): GameStatus = gameStatus

    fun getCurrentRound(): Round? = rounds.fold(null) { acc: Round?, e: Round ->
     if (acc != null && acc.getRoundNumber() > e.getRoundNumber()) acc else e
    }
}