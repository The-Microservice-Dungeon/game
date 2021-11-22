package microservice.dungeon.game.aggregates.game.domain

import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.player.Player.Player
import org.hibernate.Hibernate
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank



@Entity
@Table(name = "games", indexes = [
    Index(name = "GameIndex", columnList = "gameId", unique = true)
])
class Game(
    @Id
    @Type(type="uuid-char")
    private val gameId: UUID = UUID.randomUUID(),
    private var gameStatus: GameStatus = GameStatus.CREATED,
    @get: NotBlank
    private var maxPlayers: Int? = null,
    @get: NotBlank
    private var maxRounds: Int? = null,

    private var createdGameDateTime: LocalDateTime = LocalDateTime.now(), //can be deleted
    private var startTime: LocalTime? = null,


    private var gameTime: LocalTime? = null,
    private var lastRoundStartedAt: LocalTime? = null,
    private var roundTime: LocalTime? = null,
    private var currentRoundCount: Int = 0,

    @OneToMany
    val playerList: MutableList<Player> = mutableListOf(),



    )   {


    fun startGame() {
        if (gameStatus != GameStatus.CREATED) {
            throw MethodNotAllowedForStatusException("Game Status is $gameStatus but requires ${GameStatus.CREATED}")
        }
        gameStatus = GameStatus.GAME_RUNNING
        this.startTime = LocalTime.now()
    }


    fun endGame() {
        if (gameStatus != GameStatus.GAME_RUNNING) {
            throw MethodNotAllowedForStatusException("Game Status is $gameStatus but requires ${GameStatus.GAME_RUNNING}")
        }
        gameStatus = GameStatus.GAME_FINISHED
    }


    fun getGameId(): UUID = gameId
    fun getMaxPlayers(): Int? = maxPlayers
    fun getMaxRounds(): Int? = maxRounds
    fun getGameStatus(): GameStatus = gameStatus
    fun setCurrentRoundCount(updateCurrentRound : Int) {
        this.currentRoundCount = updateCurrentRound
    }
    fun getCurrentRoundCount() = currentRoundCount
    fun getGameTime() = gameTime

    fun setLastRoundStrartedAt(lastRoundStartedAt: LocalTime) {
        this.lastRoundStartedAt = lastRoundStartedAt
    }


    fun getPlayersUUID(): UUID{
        val playerUUID = playerList.last()
        return playerUUID.playerId
    }

    fun getLastRoundStrartedAt(): LocalTime? = lastRoundStartedAt

}

@Entity
data class GameTime(val currentGameTimeInMinutes: Long, val roundTimeInSeconds: Long, val currentRoundCount: Int) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    open var id: Long? = null
}