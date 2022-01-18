package microservice.dungeon.game.aggregates.command.controller.dto

import java.util.*

class CommandDto(
    val gameId: UUID,
    val playerToken: UUID,
    val robotId: UUID?,
    val commandType: String,
    val commandObject: CommandObjectDto
) {
    override fun equals(other: Any?): Boolean =
        (other is CommandDto)
                && gameId == other.gameId
                && playerToken == other.playerToken
                && robotId == other.robotId
                && commandType == other.commandType
                && commandObject == other.commandObject
}