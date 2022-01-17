package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class FightCommandDto(
    val robotId: UUID,
    val targetId: UUID,
    val transactionId: UUID
) {
    companion object {
        const val stringPrefix = "fight"

        fun makeFromCommand(command: Command) = FightCommandDto(
            command.getRobot()!!.getRobotId(),
            command.getCommandPayload().getTargetId()!!,
            command.getCommandId()
        )

        fun makeFromSerializedString(serializedString: String): FightCommandDto {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return FightCommandDto(
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
        (other is FightCommandDto)
                && robotId == other.robotId
                && targetId == other.targetId
                && transactionId == other.transactionId
}