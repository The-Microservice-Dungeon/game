package microservice.dungeon.game.aggregates.command.controller.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandType
import java.util.*

class RoundCommandResponseDto (
    val transactionId: UUID,
    val gameId: UUID,
    val playerId: UUID,
    val robotId: UUID?,
    val commandType: String,
    val commandObject: CommandObjectRequestDto
) {
    constructor(gameId: UUID, command: Command): this (
        transactionId = command.getCommandId(),
        gameId = gameId,
        playerId = command.getPlayer().getPlayerId(),
        robotId = command.getRobot()?.getRobotId(),
        commandType = CommandType.getStringFromType(command.getCommandType()),
        CommandObjectRequestDto(command)
    )

    override fun equals(other: Any?): Boolean =
        (other is RoundCommandResponseDto)
                && transactionId == other.transactionId
                && gameId == other.gameId
                && playerId == other.playerId
                && robotId == other.robotId
                && commandType == other.commandType
                && commandObject == other.commandObject
}