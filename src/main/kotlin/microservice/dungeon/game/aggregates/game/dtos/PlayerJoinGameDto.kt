package microservice.dungeon.game.aggregates.game.dtos

import java.util.*

class PlayerJoinGameDto constructor(
    val transactionId: UUID
) {
    override fun equals(other: Any?): Boolean =
        (other is PlayerJoinGameDto) &&
                transactionId == other.transactionId

    override fun hashCode(): Int {
        return transactionId.hashCode()
    }
}