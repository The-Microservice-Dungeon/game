package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class BuyCommandDTO(
    transactionId: UUID,
    playerId: UUID
// TODO Waiting on Trading to tell us what they need in the payload
) {
    companion object {
        fun fromCommand(command: Command): SellCommandDTO {
            return SellCommandDTO(command.transactionId, command.playerId)
        }
    }
}