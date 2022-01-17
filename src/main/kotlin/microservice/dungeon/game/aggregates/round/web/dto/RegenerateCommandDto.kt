package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class RegenerateCommandDto(
    val robotId: UUID,
    val transactionId: UUID
) {
    companion object {
        const val stringPrefix = "regenerate"

        fun makeFromCommands(command: Command) = RegenerateCommandDto(
            command.getRobot()!!.getRobotId(),
            command.getCommandId()
        )

        fun makeFromSerializedString(serializedString: String): RegenerateCommandDto {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return RegenerateCommandDto(
                UUID.fromString(explodedString[1]),
                UUID.fromString(explodedString[2])
            )
        }
    }

    override fun toString(): String {
        return "$stringPrefix $robotId $transactionId"
    }

    override fun equals(other: Any?): Boolean =
        (other is RegenerateCommandDto)
                && robotId == other.robotId
                && transactionId == other.transactionId
}