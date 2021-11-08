package microservice.dungeon.game.aggregates.round.dtos

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import java.time.LocalDateTime
import java.util.*

class RoundEventDto constructor(
    val id: UUID,
    val occurredAt: LocalDateTime,
    val roundNumber: Int,
    val roundStatus: RoundStatus
): EventDto {

    override fun serialize(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }
}