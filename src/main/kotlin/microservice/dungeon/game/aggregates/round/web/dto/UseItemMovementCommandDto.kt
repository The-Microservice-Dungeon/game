package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class UseItemMovementCommandDto(
    val robotId: UUID,
    val itemName: String,
    val transactionId: UUID
) {
    companion object {
        const val stringPrefix = "use-item-movement"

        fun makeFromCommand(command: Command) = UseItemMovementCommandDto(
            command.getRobot()!!.getRobotId(),
            command.getCommandPayload().getItemName()!!,
            command.getCommandId()
        )

        fun makeFromSerializedString(serializedString: String): UseItemMovementCommandDto {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return UseItemMovementCommandDto(
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
        (other is UseItemMovementCommandDto)
                && robotId == other.robotId
                && itemName == other.itemName
                && transactionId == other.transactionId
}