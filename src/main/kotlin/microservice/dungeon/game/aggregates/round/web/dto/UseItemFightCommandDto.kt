package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandParsingException
import mu.KotlinLogging
import java.util.*

class UseItemFightCommandDto(
    val robotId: UUID,
    val itemName: String,
    val targetId: UUID,
    val transactionId: UUID
) {
    companion object {
        private val logger = KotlinLogging.logger {}
        const val stringPrefix = "use-item-fighting"

        fun makeFromCommand(command: Command): UseItemFightCommandDto {
            return try {
                UseItemFightCommandDto(
                    command.getRobot()!!.getRobotId(),
                    command.getCommandPayload().getItemName()!!,
                    command.getCommandPayload().getTargetId()!!,
                    command.getCommandId()
                )
            } catch (e: Exception) {
                logger.error("Failed to parse Command as UseItemFightCommandDto. [commandId=${command.getCommandId()}]")
                logger.error(e.message)
                throw CommandParsingException("Failed to parse Command as UseItemFightCommandDto.")
            }
        }

        fun makeFromSerializedString(serializedString: String): UseItemFightCommandDto {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return UseItemFightCommandDto(
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
        (other is UseItemFightCommandDto)
                && robotId == other.robotId
                && itemName == other.itemName
                && targetId == other.targetId
                && transactionId == other.transactionId
}