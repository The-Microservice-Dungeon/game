package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandParsingException
import mu.KotlinLogging
import java.util.*

class BlockCommandDto(
    val robotId: UUID,
    val transactionId: UUID
) {
    companion object {
        private val logger = KotlinLogging.logger {}
        const val stringPrefix = "block"

        fun makeFromCommand(command: Command): BlockCommandDto {
            return try {
                BlockCommandDto(command.getRobot()!!.getRobotId(), command.getCommandId())
            } catch (e: Exception) {
                logger.warn("Failed to parse Command as BlockCommandDto. [commandId={}]", command.getCommandId())
                logger.warn{e.message}
                logger.warn{command.toString()}
                throw CommandParsingException("Failed to parse Command as BlockCommandDto.")
            }
        }

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