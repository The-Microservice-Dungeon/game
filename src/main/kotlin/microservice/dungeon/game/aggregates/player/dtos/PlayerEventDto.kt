package microservice.dungeon.game.aggregates.player.dtos

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import java.util.*

class PlayerEventDto (
    private val playerId: UUID,
    private val userName: String,
    private val mailAddress: String

): EventDto {

    override fun serialize(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }
}