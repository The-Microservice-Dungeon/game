package microservice.dungeon.game.aggregates.command.controller.dto

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.round.events.dto.RoundStatusEventDto
import java.util.*

class CommandRequestDto(
    val gameId: UUID,
    val playerToken: UUID,
    val robotId: UUID?,
    val commandType: String,
    val commandObject: CommandObjectRequestDto
) {
    companion object {
        private val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }

    fun serialize(): String {
        return objectMapper.writeValueAsString(this)
    }

    override fun equals(other: Any?): Boolean =
        (other is CommandRequestDto)
                && gameId == other.gameId
                && playerToken == other.playerToken
                && robotId == other.robotId
                && commandType == other.commandType
                && commandObject == other.commandObject
}