package microservice.dungeon.game.aggregates.game.controller.dto

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

class CreateGameResponseDto (
    val gameId: UUID
) {
    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }
    fun serialize(): String {
        return objectMapper.writeValueAsString(this)
    }

    override fun toString(): String =
        "CreateGameResponseDto(gameId=$gameId)"
}