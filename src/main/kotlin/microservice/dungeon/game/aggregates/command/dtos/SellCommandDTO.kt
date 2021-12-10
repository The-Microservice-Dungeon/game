package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class SellCommandDTO(
    transactionId: UUID,
    playerId: UUID,
    payload: TradingPayload
) {
    companion object {
        fun fromCommand(command: Command): SellCommandDTO {
            return SellCommandDTO(
                command.transactionId,
                command.playerId,
                TradingPayload(
                    "sell",
                    command.commandObject.ItemQuantity!!,
                    command.commandObject.planetId!!,
                    command.commandObject.itemName!!
                )
            )
        }
    }
}