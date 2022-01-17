package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class MineCommandDto(
    val robotId: UUID,
    val transactionId: UUID
) {
    companion object {
        const val stringPrefix = "mine"

        fun makeFromCommand(command: Command) = MineCommandDto(
            command.getRobot()!!.getRobotId(),
            command.getCommandId()
        )

        fun fromString(serializedString: String): MineCommandDto {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return MineCommandDto(
                UUID.fromString(explodedString[1]),
                UUID.fromString(explodedString[2])
            )
        }
    }

    override fun toString(): String {
        return "$stringPrefix $robotId $transactionId"
    }

    override fun equals(other: Any?): Boolean =
        (other is MineCommandDto)
                && robotId == other.robotId
                && transactionId == other.transactionId
}