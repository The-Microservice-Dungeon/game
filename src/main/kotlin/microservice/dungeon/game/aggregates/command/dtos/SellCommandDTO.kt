package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class SellCommandDTO(
    val transactionId: UUID,
    val playerId: UUID,
    val payload: TradingPayload
) {
    companion object {
        fun fromCommand(command: Command): SellCommandDTO {
            return SellCommandDTO(
                command.transactionId,
                command.playerId,
                TradingPayload(
                    "sell",
                    command.commandObject.itemQuantity!!,
                    command.commandObject.planetId!!,
                    command.commandObject.itemName!!
                )
            )
        }
    }

    override fun equals(other: Any?): Boolean =
        (other is SellCommandDTO)
            && transactionId == other.transactionId
            && playerId == other.playerId
            && payload == other.payload
}