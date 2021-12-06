package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class FightCommandDto(
    val robotId: UUID,
    val targetId: UUID,
    val transactionId: UUID
) {
    companion object {
        fun fromCommand(command: Command) = FightCommandDto(
            command.robotId,
            command.commandObject.targetId!!,
            command.transactionId
        )
    }

    override fun toString(): String {
        return "fight $robotId $targetId $transactionId"
    }
}