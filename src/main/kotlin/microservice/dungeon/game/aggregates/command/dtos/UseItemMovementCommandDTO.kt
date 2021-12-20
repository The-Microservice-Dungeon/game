package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class UseItemMovementCommandDTO(
    val robotId: UUID,
    val itemName: String,
    val transactionId: UUID
) {
    companion object {
        const val stringPrefix = "use-item-movement"

        fun fromCommand(command: Command) = UseItemMovementCommandDTO(
            command.robotId,
            command.commandObject.itemName!!,
            command.transactionId
        )

        fun fromString(serializedString: String): UseItemMovementCommandDTO {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return UseItemMovementCommandDTO(
                UUID.fromString(explodedString[1]),
                explodedString[2],
                UUID.fromString(explodedString[3])
            )
        }
    }

    override fun toString(): String {
        return "$stringPrefix $robotId $itemName $transactionId"
    }

    override fun equals(other: Any?): Boolean =
        (other is UseItemMovementCommandDTO)
                && robotId == other.robotId
                && itemName == other.itemName
                && transactionId == other.transactionId
}