package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class BlockCommandDTO(
    val robotId: UUID,
    val transactionId: UUID
) {
    companion object {
        fun fromCommand(command: Command) = BlockCommandDTO(
            command.robotId,
            command.transactionId
        )
    }

    override fun toString(): String {
        return "block $robotId $transactionId"
    }
}