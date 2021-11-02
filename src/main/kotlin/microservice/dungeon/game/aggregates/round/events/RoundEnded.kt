package microservice.dungeon.game.aggregates.round.events

import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import java.time.LocalDateTime

data class RoundEnded(
    val roundNumber: Int,
    val occurredOn: LocalDateTime,
    val roundStatus: RoundStatus
)