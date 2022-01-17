package microservice.dungeon.game.aggregates.command.controller.dto

import microservice.dungeon.game.aggregates.command.domain.CommandType
import java.util.*

class CommandRequestDto(
    val gameId: UUID,
    val playerToken: UUID,
    val robotId: UUID?,
    val commandType: String,
    val commandObject: CommandObjectRequestDto
) {
    override fun equals(other: Any?): Boolean =
        (other is CommandRequestDto)
                && gameId == other.gameId
                && playerToken == other.playerToken
                && robotId == other.robotId
                && commandType == other.commandType
                && commandObject == other.commandObject
}