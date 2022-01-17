package microservice.dungeon.game.aggregates.round.web.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.dtos.TradingPayload
import java.util.*

class BuyCommandDTO(
    val transactionId: UUID,
    val playerId: UUID,
    val payload: TradingPayload
) {
    companion object {
        fun fromCommand(command: Command): BuyCommandDTO {
            return BuyCommandDTO(
                command.transactionId,
                command.playerId,
                TradingPayload(
                    "buy",
                    command.commandObject.itemQuantity!!,
                    command.commandObject.planetId!!,
                    command.commandObject.itemName!!
                )
            )
        }
    }

    override fun equals(other: Any?): Boolean =
        (other is BuyCommandDTO)
                && transactionId == other.transactionId
                && playerId == other.playerId
                && payload == other.payload
}