package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.dtos.TradingPayload
import java.util.*

class BuyCommandDto(
    val transactionId: UUID,
    val playerId: UUID,
    val payload: TradingPayload
) {
    companion object {
        fun makeFromCommand(command: Command): BuyCommandDto {
            return BuyCommandDto(
                command.getCommandId(),
                command.getPlayer().getPlayerId(),
                TradingPayload(
                    "buy",
                    command.getCommandPayload().getItemQuantity()!!,
                    command.getCommandPayload().getPlanetId()!!,
                    command.getCommandPayload().getItemName()!!
                )
            )
        }
    }

    override fun equals(other: Any?): Boolean =
        (other is BuyCommandDto)
                && transactionId == other.transactionId
                && playerId == other.playerId
                && payload == other.payload
}