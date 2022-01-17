package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandParsingException
import mu.KotlinLogging
import java.util.*

class RegenerateCommandDto(
    val robotId: UUID,
    val transactionId: UUID
) {
    companion object {
        private val logger = KotlinLogging.logger {}
        const val stringPrefix = "regenerate"

        fun makeFromCommands(command: Command):RegenerateCommandDto {
            return try {
                RegenerateCommandDto(
                    command.getRobot()!!.getRobotId(),
                    command.getCommandId()
                )
            } catch (e: Exception) {
                logger.error("Failed to parse Command as RegenerateCommandDto. [commandId=${command.getCommandId()}]")
                throw CommandParsingException("Failed to parse Command as RegenerateCommandDto.")
            }
        }

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