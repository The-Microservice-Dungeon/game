package microservice.dungeon.game.aggregates.round.web.dto
/*
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandParsingException
import mu.KotlinLogging
import java.util.*

class UseItemRepairCommandDto(
    val robotId: UUID,
    val itemName: String,
    val transactionId: UUID
) {
    companion object {
        private val logger = KotlinLogging.logger {}
        const val stringPrefix = "use-item-repair"

        fun makeFromCommand(command: Command): UseItemRepairCommandDto {
            return try {
                UseItemRepairCommandDto(
                    command.getRobot()!!.getRobotId(),
                    command.getCommandPayload()!!.getItemName()!!,
                    command.getCommandId()
                )
            } catch (e: Exception) {
                logger.warn("Failed to parse Command as UseItemRepairCommandDto. [commandId={}]", command.getCommandId())
                logger.warn{e.message}
                logger.warn{command.toString()}
                throw CommandParsingException("Failed to parse Command as UseItemRepairCommandDto.")
            }
        }

        fun makeFromSerializedString(serializedString: String): UseItemRepairCommandDto {
            val explodedString = serializedString.split(" ")
            if (explodedString[0] != stringPrefix) {
                throw IllegalArgumentException(explodedString[0])
            }
            return UseItemRepairCommandDto(
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
        (other is UseItemRepairCommandDto)
                && robotId == other.robotId
                && itemName == other.itemName
                && transactionId == other.transactionId
}*/