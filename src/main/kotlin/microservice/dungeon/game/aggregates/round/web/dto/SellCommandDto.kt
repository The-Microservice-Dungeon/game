package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandParsingException
import microservice.dungeon.game.aggregates.command.dtos.TradingPayload
import mu.KotlinLogging
import java.util.*

class SellCommandDto(
    val transactionId: UUID,
    val playerId: UUID,
    val payload: TradingPayload
) {
    companion object {
        private val logger = KotlinLogging.logger {}

        fun makeFromCommand(command: Command): SellCommandDto {
            return try {
                SellCommandDto(
                    command.getCommandId(),
                    command.getPlayer().getPlayerId(),
                    TradingPayload(
                        "sell",
                        command.getCommandPayload().getItemQuantity()!!,
                        command.getCommandPayload().getPlanetId()!!,
                        command.getCommandPayload().getItemName()!!
                    )
                )
            } catch (e: Exception) {
                logger.error("Failed to parse Command as SellCommandDto. [commandId=${command.getCommandId()}]")
                throw CommandParsingException("Failed to parse Command as SellCommandDto.")
            }
        }
    }

    override fun equals(other: Any?): Boolean =
        (other is SellCommandDto)
            && transactionId == other.transactionId
            && playerId == other.playerId
            && payload == other.payload
}