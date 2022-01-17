package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandParsingException
import microservice.dungeon.game.aggregates.command.dtos.TradingPayload
import mu.KotlinLogging
import java.util.*

class BuyCommandDto(
    val transactionId: UUID,
    val playerId: UUID,
    val payload: TradingPayload
) {
    companion object {
        private val logger = KotlinLogging.logger {}

        fun makeFromCommand(command: Command): BuyCommandDto {
            return try {
                BuyCommandDto(
                    command.getCommandId(),
                    command.getPlayer().getPlayerId(),
                    TradingPayload(
                        "buy",
                        command.getCommandPayload().getItemQuantity()!!,
                        command.getCommandPayload().getPlanetId()!!,
                        command.getCommandPayload().getItemName()!!
                    )
                )
            } catch (e: Exception) {
                logger.error("Failed to parse Command as BuyCommandDto. [commandId=${command.getCommandId()}]")
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