package microservice.dungeon.game.aggregates.game.domain

import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table


@Entity
@Table(name = "games", indexes = [
    Index(name = "GameIndex", columnList = "gameId", unique = true)
])
class Game(
    @Id
    @Type(type="uuid-char")
    private val gameId: UUID = UUID.randomUUID(),
    private var gameStatus: GameStatus = GameStatus.IN_PREPARATION,
    private val currentRoundNumber: Int,
    private val currentRoundId: UUID
)   {

    fun startGame() {
        if (gameStatus != GameStatus.IN_PREPARATION) {
            throw MethodNotAllowedForStatusException("Round Status is $gameStatus but requires ${GameStatus.IN_PREPARATION}")
        }
        gameStatus = GameStatus.GAME_RUNNING
    }
    fun endGame() {
        if (gameStatus != GameStatus.GAME_RUNNING) {
            throw MethodNotAllowedForStatusException("Round Status is $gameStatus but requires ${GameStatus.GAME_RUNNING}")
        }
        gameStatus = GameStatus.GAME_FINISHED
    }


    fun getGameId(): UUID = gameId

    fun getCurrentRoundNumber(): Int = currentRoundNumber
    fun getCurrentRoundId(): UUID = currentRoundId

    fun getGameStatus(): GameStatus = gameStatus
}