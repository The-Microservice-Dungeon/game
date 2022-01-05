package microservice.dungeon.game.aggregates.game.domain

import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.player.domain.Player
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
    private var maxPlayers: Int = 0,
    @get: NotBlank
    private var maxRounds: Int = 0,

    private var roundDuration: Long = 60000, // in ms
    private var commandCollectDuration: Double = 45000.00, // in ms

    private var createdGameDateTime: LocalDateTime = LocalDateTime.now(), //can be deleted
    private var startTime: LocalTime? = null,

    private var gameTime: LocalTime? = null,
    private var lastRoundStartedAt: LocalTime? = null,
    private var currentRoundCount: Int = 0,

    @OneToMany(cascade = [CascadeType.ALL],
        mappedBy = "playerId",
        orphanRemoval = true)
//    @JoinColumn(name = "game_gameId")
    val playerList: MutableList<PlayersInGame> = mutableListOf(),

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
    fun getMaxPlayers(): Int = maxPlayers
    fun getMaxRounds(): Int = maxRounds
    fun getRoundDuration(): Long = roundDuration
    fun getCommandCollectDuration(): Double = commandCollectDuration
    fun getGameStatus(): GameStatus = gameStatus
    fun setCurrentRoundCount(updateCurrentRound : Int) {
        this.currentRoundCount = updateCurrentRound
    }
    fun getCurrentRoundCount() = currentRoundCount
    fun getGameStartTime() = startTime
    fun getCreatedGameDateTime() = createdGameDateTime

    fun setLastRoundStartedAt(lastRoundStartedAt: LocalTime) {
        this.lastRoundStartedAt = lastRoundStartedAt
    }
    fun getLastRoundStartedAt(): LocalTime? = lastRoundStartedAt

    fun getPlayersInGame(): MutableList<PlayersInGame> = playerList

    fun setMaxRounds(newMaxRounds: Int?) {
        if (newMaxRounds != null) {
            this.maxRounds = newMaxRounds
        }
    }
    fun setRoundDuration(newRoundDuration: Long?) {
        if (newRoundDuration != null) {
            this.roundDuration = newRoundDuration
        }
    }

    fun setMaxPlayers(maxPlayers: Int) {
        this.maxPlayers = maxPlayers
    }

    fun setCommandCollectDuration(l: Double) {
        this.commandCollectDuration = l
    }

    fun addPlayersToGame(newPlayer: PlayersInGame) {
        playerList += newPlayer
    }

}

@Entity
data class PlayersInGame(
    @Id
    @Type(type="uuid-char")
    private val playerId: UUID,

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gameId", nullable = false)
    @Type(type="uuid-char")
    var gameId: UUID
){

}