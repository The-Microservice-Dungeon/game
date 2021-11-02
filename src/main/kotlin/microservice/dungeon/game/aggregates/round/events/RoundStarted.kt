package microservice.dungeon.game.aggregates.round.events

import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import java.time.LocalDateTime
import java.util.*

data class RoundStarted(
    val roundNumber: Int,
    val occurredOn: LocalDateTime,
    val roundStatus: RoundStatus
)