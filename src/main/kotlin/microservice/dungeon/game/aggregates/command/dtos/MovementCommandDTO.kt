package microservice.dungeon.game.aggregates.command.dtos

import microservice.dungeon.game.aggregates.command.domain.Command
import java.util.*

class MovementCommandDTO(
    val robotId: UUID,
    val planetId: UUID,
    val transactionId: UUID
) {
    companion object {
        fun fromCommand(command: Command) = FightCommandDTO(
            command.robotId,
            command.commandObject.planetId!!,
            command.transactionId
        )
    }

    override fun toString(): String {
        return "move $robotId $planetId $transactionId"
    }
}