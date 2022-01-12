package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.CommandObject
import microservice.dungeon.game.aggregates.command.domain.CommandType
import java.util.*

class CommandDTO(
    val gameId: UUID,

    val playerToken: UUID,

    val robotId: UUID?,

    val commandType: CommandType,

    val commandObject: CommandObject
) {
    override fun equals(other: Any?): Boolean =
        (other is CommandDTO)
                && gameId == other.gameId
                && playerToken == other.playerToken
                && robotId == other.robotId
                && commandType == other.commandType
                && commandObject == other.commandObject
}