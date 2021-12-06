package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class UseItemRepairCommandDto(
    val robotId: UUID,
    val itemName: String,
    val transactionId: UUID
) {
    companion object {
        fun fromCommand(command: Command) = UseItemRepairCommandDto(
            command.robotId,
            command.commandObject.itemName!!,
            command.transactionId
        )
    }

    override fun toString(): String {
        return "use-item-repair $robotId $itemName $transactionId"
    }
}