package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandParsingException
import mu.KotlinLogging
import java.util.*

class MovementCommandDto(
    val robotId: UUID,
    val planetId: UUID,
    val transactionId: UUID
) {
    companion object {
        private val logger = KotlinLogging.logger {}
        const val stringPrefix = "move"

        fun makeFromCommand(command: Command): MovementCommandDto {
            return try {
                MovementCommandDto(
                    command.getRobot()!!.getRobotId(),
                    command.getCommandPayload()!!.getPlanetId()!!,
                    command.getCommandId()
                )
            } catch (e: Exception) {
                logger.warn("Failed to parse Command as MovementCommandDto. [commandId={}]", command.getCommandId())
                logger.warn{e.message}
                logger.warn{command.toString()}
                throw CommandParsingException("Failed to parse Command as MovementCommandDto.")
            }
        }

        fun fromString(serializedString: String): MovementCommandDto {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return MovementCommandDto(
                UUID.fromString(explodedString[1]),
                UUID.fromString(explodedString[2]),
                UUID.fromString(explodedString[3])
            )
        }
    }

    override fun toString(): String {
        return "$stringPrefix $robotId $planetId $transactionId"
    }

    override fun equals(other: Any?): Boolean =
        (other is MovementCommandDto)
                && robotId == other.robotId
                && planetId == other.planetId
                && transactionId == other.transactionId
}