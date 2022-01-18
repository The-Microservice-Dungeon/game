package microservice.dungeon.game.aggregates.command.controller.dto

import java.util.*

class CommandObjectDto(
    val commandType: String,
    val planetId: UUID?,
    val targetId: UUID?,
    val itemName: String?,
    val itemQuantity: Int?
) {
    override fun equals(other: Any?): Boolean =
        (other is CommandObjectDto)
                && commandType == other.commandType
                && planetId == other.planetId
                && targetId == other.targetId
                && itemName == other.itemName
                && itemQuantity == other.itemQuantity
}