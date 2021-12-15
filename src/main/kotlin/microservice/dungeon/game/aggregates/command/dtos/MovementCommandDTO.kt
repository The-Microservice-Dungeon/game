package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class MovementCommandDTO(
    val robotId: UUID,
    val planetId: UUID,
    val transactionId: UUID
) {
    companion object {
        const val stringPrefix = "move"

        fun fromCommand(command: Command) = MovementCommandDTO(
            command.robotId,
            command.commandObject.planetId!!,
            command.transactionId
        )

        fun fromString(serializedString: String): MovementCommandDTO {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return MovementCommandDTO(
                UUID.fromString(explodedString[1]),
                UUID.fromString(explodedString[2]),
                UUID.fromString(explodedString[3])
            )
        }
    }

    override fun toString(): String {
        return "$stringPrefix $robotId $planetId $transactionId"
    }

    override fun equals(other: Any?): Boolean =
        (other is MovementCommandDTO)
                && robotId == other.robotId
                && planetId == other.planetId
                && transactionId == other.transactionId
}