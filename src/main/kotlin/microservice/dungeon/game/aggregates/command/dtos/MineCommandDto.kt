package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class MineCommandDto(
    val robotId: UUID,
    val transactionId: UUID
) {
    companion object {
        fun fromCommand(command: Command) = MineCommandDto(
            command.robotId,
            command.transactionId
        )
    }

    override fun toString(): String {
        return "mine $robotId $transactionId"
    }
}