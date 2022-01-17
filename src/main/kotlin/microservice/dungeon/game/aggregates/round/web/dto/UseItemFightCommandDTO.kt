package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class UseItemFightCommandDTO(
    val robotId: UUID,
    val itemName: String,
    val targetId: UUID,
    val transactionId: UUID
) {
    companion object {
        const val stringPrefix = "use-item-fighting"

        fun fromCommand(command: Command) = UseItemFightCommandDTO(
            command.robotId!!,
            command.commandObject.itemName!!,
            command.commandObject.targetId!!,
            command.transactionId
        )

        fun fromString(serializedString: String): UseItemFightCommandDTO {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return UseItemFightCommandDTO(
                UUID.fromString(explodedString[1]),
                explodedString[2],
                UUID.fromString(explodedString[3]),
                UUID.fromString(explodedString[4])
            )
        }
    }

    override fun toString(): String {
        return "$stringPrefix $robotId $itemName $targetId $transactionId"
    }

    override fun equals(other: Any?): Boolean =
        (other is UseItemFightCommandDTO)
                && robotId == other.robotId
                && itemName == other.itemName
                && targetId == other.targetId
                && transactionId == other.transactionId
}