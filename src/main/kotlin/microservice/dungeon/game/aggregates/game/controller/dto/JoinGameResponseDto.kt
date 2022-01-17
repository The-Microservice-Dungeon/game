package microservice.dungeon.game.aggregates.game.controller.dto

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

class JoinGameResponseDto(
    val transactionId: UUID
) {
    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }
    fun serialize(): String {
        return objectMapper.writeValueAsString(this)
    }
}