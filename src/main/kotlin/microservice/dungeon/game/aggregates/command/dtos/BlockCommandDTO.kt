package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class BlockCommandDTO(
    val robotId: UUID,
    val transactionId: UUID
) {
    companion object {
        const val stringPrefix = "block"

        fun fromCommand(command: Command) = BlockCommandDTO(
            command.robotId!!,
            command.transactionId
        )

        fun fromString(serializedString: String): BlockCommandDTO {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return BlockCommandDTO(
                UUID.fromString(explodedString[1]),
                UUID.fromString(explodedString[2])
            )
        }
    }

    override fun toString(): String {
        return "$stringPrefix $robotId $transactionId"
    }

    override fun equals(other: Any?): Boolean =
        (other is BlockCommandDTO)
                && robotId == other.robotId
                && transactionId == other.transactionId
}