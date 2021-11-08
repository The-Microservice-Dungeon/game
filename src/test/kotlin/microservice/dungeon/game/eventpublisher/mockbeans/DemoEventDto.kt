package microservice.dungeon.game.eventpublisher.mockbeans

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import java.time.LocalDateTime
import java.util.*

class DemoEventDto constructor(
    val id: UUID,
    val topic: String,
    val occurredAt: LocalDateTime
): EventDto {
    override fun serialize(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }
}