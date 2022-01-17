package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class BlockCommandDto(
    val robotId: UUID,
    val transactionId: UUID
) {
    companion object {
        const val stringPrefix = "block"

        fun makeFromCommand(command: Command) = BlockCommandDto(
            command.getRobot()!!.getRobotId(), command.getCommandId()
        )

        fun makeFromSerializedString(serializedString: String): BlockCommandDto {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return BlockCommandDto(
                UUID.fromString(explodedString[1]),
                UUID.fromString(explodedString[2])
            )
        }
    }

    override fun toString(): String {
        return "$stringPrefix $robotId $transactionId"
    }

    override fun equals(other: Any?): Boolean =
        (other is BlockCommandDto)
                && robotId == other.robotId
                && transactionId == other.transactionId
}