package microservice.dungeon.game.aggregates.command.controller.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandType
import java.util.*

class CommandObjectRequestDto(
    val commandType: String,
    val planetId: UUID?,
    val targetId: UUID?,
    val itemName: String?,
    val itemQuantity: Int?
) {
    constructor(command: Command): this (
        commandType = CommandType.getStringFromType(command.getCommandType()),
        planetId = command.getCommandPayload().getPlanetId(),
        targetId = command.getCommandPayload().getTargetId(),
        itemName = command.getCommandPayload().getItemName(),
        itemQuantity = command.getCommandPayload().getItemQuantity()
    )

    override fun toString(): String =
        "CommandObjectRequestDto(commandType='${commandType}', planetId=${planetId}, targetId=${targetId}, " +
        "itemName='${itemName}', itemQuantity=${itemQuantity})"

    override fun equals(other: Any?): Boolean =
        (other is CommandObjectRequestDto)
                && commandType == other.commandType
                && planetId == other.planetId
                && targetId == other.targetId
                && itemName == other.itemName
                && itemQuantity == other.itemQuantity
}