package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.CommandPayload
import microservice.dungeon.game.aggregates.command.domain.CommandType
import java.util.*

class CommandDTO(
    val gameId: UUID,

    val playerToken: UUID,

    val robotId: UUID?,

    val commandType: CommandType,

    val commandPayload: CommandPayload
) {
    override fun equals(other: Any?): Boolean =
        (other is CommandDTO)
                && gameId == other.gameId
                && playerToken == other.playerToken
                && robotId == other.robotId
                && commandType == other.commandType
                && commandPayload == other.commandPayload
}