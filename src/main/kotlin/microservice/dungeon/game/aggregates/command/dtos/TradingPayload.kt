package microservice.dungeon.game.aggregates.command.dtos

import java.util.*

class TradingPayload(
    val commandType: String,
    val amount: Int,
    val planetId: UUID,
    val itemName: String
) {
    override fun equals(other: Any?): Boolean =
        (other is TradingPayload)
                && commandType == other.commandType
                && amount == other.amount
                && planetId == other.planetId
                && itemName == other.itemName
}