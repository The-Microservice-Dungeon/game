package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class UseItemMovementCommandDTO(
    val robotId: UUID,
    val itemName: String,
    val transactionId: UUID
) {
    companion object {
        fun fromCommand(command: Command) = UseItemMovementCommandDTO(
            command.robotId,
            command.commandObject.itemName!!,
            command.transactionId
        )
    }

    override fun toString(): String {
        return "use-item-movement $robotId $itemName $transactionId"
    }
}