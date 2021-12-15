package microservice.dungeon.game.contracts.round.web.robot.resources

import microservice.dungeon.game.aggregates.command.domain.CommandType
import java.util.*

data class RobotCommandInput(
    val transactionId: UUID,
    val robotId: UUID,
    val commandType: CommandType,
    val gameId: UUID = UUID.randomUUID(),
    val playerId: UUID = UUID.randomUUID(),
    val roundNumber: Int = 3,
    val planetId: UUID = UUID.randomUUID(),
    val targetId: UUID = UUID.randomUUID(),
    val itemName: String = "ANY_ITEM-NAME",
    val itemQuantity: Int = 3
)