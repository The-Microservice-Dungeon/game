package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class BuyCommandDTO(
    transactionId: UUID,
    playerId: UUID,
    payload: TradingPayload
) {
    companion object {
        fun fromCommand(command: Command): BuyCommandDTO {
            return BuyCommandDTO(
                command.transactionId,
                command.playerId,
                TradingPayload(
                    "buy",
                    command.commandObject.ItemQuantity!!,
                    command.commandObject.planetId!!,
                    command.commandObject.itemName!!
                )
            )
        }
    }
}