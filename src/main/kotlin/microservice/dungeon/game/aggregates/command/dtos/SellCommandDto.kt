package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class SellCommandDto(
    transactionId: UUID,
    playerId: UUID
// TODO Waiting on Trading to tell us what they need in the payload
) {
    companion object {
        fun fromCommand(command: Command): SellCommandDto {
            return SellCommandDto(command.transactionId, command.playerId)
        }
    }
}