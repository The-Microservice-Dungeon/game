package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandParsingException
import mu.KotlinLogging
import java.util.*

class BuyCommandDto(
    val transactionId: UUID,
    val playerId: UUID,
    val payload: TradingPayloadDto
) {
    companion object {
        private val logger = KotlinLogging.logger {}

        fun makeFromCommand(command: Command): BuyCommandDto {
            return try {
                BuyCommandDto(
                    command.getCommandId(),
                    command.getPlayer().getPlayerId(),
                    TradingPayloadDto(
                        commandType = "buy",
                        robotId = command.getRobot()?.getRobotId(),
                        amount = command.getCommandPayload()!!.getItemQuantity(),
                        planetId = command.getCommandPayload()!!.getPlanetId(),
                        itemName = command.getCommandPayload()!!.getItemName()
                    )
                )
            } catch (e: Exception) {
                logger.warn("Failed to parse Command as BuyCommandDto. [commandId={}]", command.getCommandId())
                logger.warn{e.message}
                logger.warn{command.toString()}
                throw CommandParsingException("Failed to parse Command as BuyCommandDto.")
            }
        }
    }

    override fun equals(other: Any?): Boolean =
        (other is BuyCommandDto)
                && transactionId == other.transactionId
                && playerId == other.playerId
                && payload == other.payload
}