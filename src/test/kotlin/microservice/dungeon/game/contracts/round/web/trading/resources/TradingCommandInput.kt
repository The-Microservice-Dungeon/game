package microservice.dungeon.game.contracts.round.web.trading.resources

import microservice.dungeon.game.aggregates.command.domain.CommandType
import java.util.*

data class TradingCommandInput (
    val transactionId: UUID,
    val gameId: UUID,
    val playerId: UUID,
    val commandType: CommandType,
    val amount: Int,
    val planetId: UUID,
    val itemName: String
)