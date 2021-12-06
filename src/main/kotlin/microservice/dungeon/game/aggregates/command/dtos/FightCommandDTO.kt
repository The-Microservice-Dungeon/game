package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class FightCommandDTO(
    val robotId: UUID,
    val targetId: UUID,
    val transactionId: UUID
) {
    companion object {
        const val stringPrefix = "fight"

        fun fromCommand(command: Command) = FightCommandDTO(
            command.robotId,
            command.commandObject.targetId!!,
            command.transactionId
        )

        fun fromString(serializedString: String): FightCommandDTO {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return FightCommandDTO(
                UUID.fromString(explodedString[1]),
                UUID.fromString(explodedString[2]),
                UUID.fromString(explodedString[3])
            )
        }
    }

    override fun toString(): String {
        return "$stringPrefix $robotId $targetId $transactionId"
    }

    override fun equals(other: Any?): Boolean =
        (other is FightCommandDTO)
                && robotId == robotId
                && targetId == targetId
                && transactionId == transactionId
}