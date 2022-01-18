package microservice.dungeon.game.aggregates.command.controller.dto

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.round.domain.Round
import java.util.*

class RoundCommandsResponseDto (
    val gameId: UUID,
    val roundId: UUID,
    val roundNumber: Int,
    val commands: List<RoundCommandResponseDto>
) {
    constructor(round: Round, commands: List<Command>): this (
        gameId = round.getGameId(),
        roundId = round.getRoundId(),
        roundNumber = round.getRoundNumber(),
        commands.map { RoundCommandResponseDto(round.getGameId(), it) }
    )
}