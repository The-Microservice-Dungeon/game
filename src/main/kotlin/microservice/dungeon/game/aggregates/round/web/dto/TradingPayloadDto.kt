package microservice.dungeon.game.aggregates.round.web.dto

import java.util.*

class TradingPayloadDto (
    val commandType: String,
    val robotId: UUID?,
    val amount: Int?,
    val planetId: UUID?,
    val itemName: String?
) {
    override fun equals(other: Any?): Boolean =
        (other is TradingPayloadDto)
                && commandType == other.commandType
                && amount == other.amount
                && planetId == other.planetId
                && itemName == other.itemName
}