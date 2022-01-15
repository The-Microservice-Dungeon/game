package microservice.dungeon.game.aggregates.game.controller.dto

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import java.util.*

class GameResponseDto (
    val gameId: UUID,
    val gameStatus: String,
    val maxPlayers: Int,
    val maxRounds: Int,
    val currentRoundNumber: Int?,
    val roundLengthInMillis: Long,
    val participatingPlayers: List<UUID>
) {
    constructor(game: Game): this(
        game.getGameId(),
        mapGameStatusToDtoStatus(game.getGameStatus()),
        game.getMaxPlayers(),
        game.getMaxRounds(),
        game.getCurrentRound()?.getRoundNumber(),
        game.getTotalRoundTimespanInMS(),
        game.getParticipatingPlayers().map { player -> player.getPlayerId() }
    )

    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()

        fun mapGameStatusToDtoStatus(gameStatus: GameStatus): String {
            return when(gameStatus) {
                GameStatus.CREATED -> "created"
                GameStatus.GAME_RUNNING -> "started"
                GameStatus.GAME_FINISHED -> "ended"
            }
        }
    }

    fun serialize(): String {
        return objectMapper.writeValueAsString(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameResponseDto

        if (gameId != other.gameId) return false
        if (gameStatus != other.gameStatus) return false
        if (maxPlayers != other.maxPlayers) return false
        if (maxRounds != other.maxRounds) return false
        if (currentRoundNumber != other.currentRoundNumber) return false
        if (roundLengthInMillis != other.roundLengthInMillis) return false
        if (participatingPlayers != other.participatingPlayers) return false

        return true
    }
}