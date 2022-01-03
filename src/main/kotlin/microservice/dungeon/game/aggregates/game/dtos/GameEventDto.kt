package microservice.dungeon.game.aggregates.game.dtos

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.dtos.GameEventDto
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.*

class GameEventDto(
    val gameStatus: String
): EventDto {

    constructor(gameStatus: GameStatus):
            this(GameEventDto.mapStatusToOutputStatus(gameStatus))

    override fun serialize(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }

    companion object {
        private fun mapStatusToOutputStatus(status: GameStatus): String {
            return when (status) {
                GameStatus.CREATED          -> "created"
                GameStatus.GAME_RUNNING     -> "game started"
                GameStatus.GAME_FINISHED    -> "game ended"
                else -> {
                    throw RuntimeException("invalid gameStatus -> event api status mapping")
                }
            }
        }
    }
}