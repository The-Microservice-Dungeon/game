package microservice.dungeon.game.commands.dtos

import microservice.dungeon.game.commands.domain.CommandObject
import microservice.dungeon.game.commands.domain.CommandType
import java.util.*

class CommandDTO(
    val gameId: UUID,

    val playerId: UUID,

    val robotId: UUID,

    val commandType: CommandType,

    val commandObject: CommandObject
)