package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandParsingException
import mu.KotlinLogging
import java.util.*

class FightCommandDto(
    val robotId: UUID,
    val targetId: UUID,
    val transactionId: UUID
) {
    companion object {
        private val logger = KotlinLogging.logger {}
        const val stringPrefix = "fight"

        fun makeFromCommand(command: Command): FightCommandDto {
            return try {
                FightCommandDto(
                    command.getRobot()!!.getRobotId(),
                    command.getCommandPayload().getTargetId()!!,
                    command.getCommandId()
                )
            } catch (e: Exception) {
                logger.error("Failed to parse Command as FightCommandDto. [commandId=${command.getCommandId()}]")
                logger.error(e.message)
                throw CommandParsingException("Failed to parse Command as FightCommandDto.")
            }
        }

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