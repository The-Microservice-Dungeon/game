package microservice.dungeon.game.aggregates.game.dtos

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import java.time.LocalDateTime
import java.util.*

class GameEventDto(
    val id: UUID,
    val occurredAt: EventTime,
    val gameId: UUID,
    val gameStatus: GameStatus
): EventDto {

    override fun serialize(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }
}