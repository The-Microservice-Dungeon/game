package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.CommandObject
import microservice.dungeon.game.aggregates.command.domain.CommandType
import java.util.*

class CommandDTO(
    val gameId: UUID,

    val playerId: UUID,

    val robotId: UUID,

    val commandType: CommandType,

    val commandObject: CommandObject
) {
    override fun equals(other: Any?): Boolean =
        (other is CommandDTO)
                && gameId == other.gameId
                && playerId == other.playerId
                && robotId == other.robotId
                && commandType == other.commandType
                && commandObject == other.commandObject
}