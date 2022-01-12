package microservice.dungeon.game.aggregates.command.dtos

import java.util.*

class CommandResponseDTO(
    val commandId: UUID
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommandResponseDTO

        if (commandId != other.commandId) return false

        return true
    }

    override fun hashCode(): Int {
        return commandId.hashCode()
    }
}