package microservice.dungeon.game.aggregates.game.domain

import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.player.Player.Player
import org.hibernate.annotations.Type
import java.time.*
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
    private var maxPlayers: Int = 0,
    @get: NotBlank
    private var maxRounds: Int = 0,

    private var createdGameDateTime: LocalDateTime = LocalDateTime.now(), //can be deleted
    private var startTime: LocalTime = LocalTime.now(),

    private var currentTime: LocalTime = LocalTime.now(),
    private var roundTime: LocalTime = LocalTime.now(),
    private var roundCount: Int = 0,

    @OneToMany
    val playerList: MutableList<Player> = mutableListOf<Player>()

)   {

    private fun runGame(){
        //TODO
    }

    fun startGame() {
        if (gameStatus != GameStatus.CREATED) {
            throw MethodNotAllowedForStatusException("Game Status is $gameStatus but requires ${GameStatus.CREATED}")
        }
        gameStatus = GameStatus.GAME_RUNNING
        this.runGame()
        this.startTime = LocalTime.now()
    }
    fun insertPlayer(){
        if (gameStatus != GameStatus.CREATED) {
            throw MethodNotAllowedForStatusException("For Player to join requires game status ${GameStatus.CREATED}, but game status is $gameStatus")
        }
    }


    fun endGame() {
        if (gameStatus != GameStatus.GAME_RUNNING) {
            throw MethodNotAllowedForStatusException("Game Status is $gameStatus but requires ${GameStatus.GAME_RUNNING}")
        }
        gameStatus = GameStatus.GAME_FINISHED
    }


    fun getGameId(): UUID = gameId
    fun getMaxPlayers(): Int = maxPlayers
    fun getMaxRounds(): Int = maxRounds
    fun getGameStatus(): GameStatus = gameStatus

    fun getCurrentTime(): Int {
        currentTime = LocalTime.now()
        return currentTime.compareTo(startTime)
    }
}

