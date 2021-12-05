package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.CommandObject
import microservice.dungeon.game.aggregates.command.domain.CommandType
import java.util.*

class CommandDto(
    val gameId: UUID,

    val playerId: UUID,

    val robotId: UUID,

    val commandType: CommandType,

    val commandObject: CommandObject
)