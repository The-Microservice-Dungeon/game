package microservice.dungeon.game.aggregates.game.dtos

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import java.time.LocalDateTime
import java.util.*

class GameEventDto constructor(
    val id: UUID,
    val occurredAt: LocalDateTime,
    val gameId: UUID,
    val gameStatus: GameStatus
): EventDto {

    override fun serialize(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }
}