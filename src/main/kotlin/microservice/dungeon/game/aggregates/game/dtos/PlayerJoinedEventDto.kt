package microservice.dungeon.game.aggregates.game.dtos


import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import java.util.*

class PlayerJoinedEventDto(
    val playerId: UUID
): EventDto {

    override fun serialize(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }
}