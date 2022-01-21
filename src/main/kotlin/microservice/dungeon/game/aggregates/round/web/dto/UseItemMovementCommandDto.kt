package microservice.dungeon.game.aggregates.round.web.dto

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandParsingException
import mu.KotlinLogging
import java.util.*

class UseItemMovementCommandDto(
    val robotId: UUID,
    val itemName: String,
    val transactionId: UUID
) {
    companion object {
        private val logger = KotlinLogging.logger {}
        const val stringPrefix = "use-item-movement"

        fun makeFromCommand(command: Command): UseItemMovementCommandDto {
            return try {
                UseItemMovementCommandDto(
                    command.getRobot()!!.getRobotId(),
                    command.getCommandPayload().getItemName()!!,
                    command.getCommandId()
                )
            } catch (e: Exception) {
                logger.error("Failed to parse Command as UseItemMovementCommandDto. [commandId=${command.getCommandId()}]")
                logger.error(e.message)
                logger.error(ObjectMapper().findAndRegisterModules().writeValueAsString(command))
                throw CommandParsingException("Failed to parse Command as UseItemMovementCommandDto.")
            }
        }

        fun makeFromSerializedString(serializedString: String): UseItemMovementCommandDto {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return UseItemMovementCommandDto(
                UUID.fromString(explodedString[1]),
                explodedString[2],
                UUID.fromString(explodedString[3])
            )
        }
    }

    override fun toString(): String {
        return "$stringPrefix $robotId $itemName $transactionId"
    }

    override fun equals(other: Any?): Boolean =
        (other is UseItemMovementCommandDto)
                && robotId == other.robotId
                && itemName == other.itemName
                && transactionId == other.transactionId
}