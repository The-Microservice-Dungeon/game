package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class UseItemFightCommandDto(
    val robotId: UUID,
    val itemName: String,
    val targetId: UUID,
    val transactionId: UUID
) {
    companion object {
        fun fromCommand(command: Command) = UseItemFightCommandDto(
            command.robotId,
            command.commandObject.itemName!!,
            command.commandObject.targetId!!,
            command.transactionId
        )
    }

    override fun toString(): String {
        return "use-item-fighting $robotId $itemName $targetId $transactionId"
    }
}