package microservice.dungeon.game.aggregates.command.dtos

import java.util.*

class TradingPayload(
    commandType: String,
    amount: Int,
    planetId: UUID,
    itemName: String
)