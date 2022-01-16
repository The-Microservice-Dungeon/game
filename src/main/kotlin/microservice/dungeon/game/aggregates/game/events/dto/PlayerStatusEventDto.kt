package microservice.dungeon.game.aggregates.game.events.dto

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import java.util.*

class PlayerStatusEventDto (
    val playerId: UUID,
    val name: String

) : EventDto {

    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }

    override fun serialize(): String {
        return GameStatusEventDto.objectMapper.writeValueAsString(this)
    }
}