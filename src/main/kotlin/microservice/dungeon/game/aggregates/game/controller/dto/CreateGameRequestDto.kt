package microservice.dungeon.game.aggregates.game.controller.dto

import com.fasterxml.jackson.databind.ObjectMapper

class CreateGameRequestDto (
    val maxPlayers: Int,
    val maxRounds: Int
) {
    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }
    fun serialize(): String {
        return objectMapper.writeValueAsString(this)
    }

    override fun toString(): String =
        "CreateGameRequestDto(maxPlayers=$maxPlayers, maxRounds=$maxRounds)"
}