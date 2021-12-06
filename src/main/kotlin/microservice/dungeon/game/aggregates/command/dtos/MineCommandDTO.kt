package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class MineCommandDTO(
    val robotId: UUID,
    val transactionId: UUID
) {
    companion object {
        fun fromCommand(command: Command) = MineCommandDTO(
            command.robotId,
            command.transactionId
        )
    }

    override fun toString(): String {
        return "mine $robotId $transactionId"
    }
}