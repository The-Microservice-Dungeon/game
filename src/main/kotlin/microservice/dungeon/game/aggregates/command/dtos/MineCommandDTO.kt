package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class MineCommandDTO(
    val robotId: UUID,
    val transactionId: UUID
) {
    companion object {
        const val stringPrefix = "mine"

        fun fromCommand(command: Command) = MineCommandDTO(
            command.robotId!!,
            command.transactionId
        )

        fun fromString(serializedString: String): MineCommandDTO {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return MineCommandDTO(
                UUID.fromString(explodedString[1]),
                UUID.fromString(explodedString[2])
            )
        }
    }

    override fun toString(): String {
        return "$stringPrefix $robotId $transactionId"
    }

    override fun equals(other: Any?): Boolean =
        (other is MineCommandDTO)
                && robotId == other.robotId
                && transactionId == other.transactionId
}