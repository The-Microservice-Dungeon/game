package microservice.dungeon.game.aggregates.round.events

import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import java.time.LocalDateTime
import java.util.*

class RoundStarted(
    id: UUID,
    occurredAt: EventTime,
    roundId: UUID,
    gameId: UUID,
    roundNumber: Int,
    roundStatus: RoundStatus
) : AbstractRoundEvent(id, occurredAt, roundId, gameId, roundNumber, roundStatus, "roundStarted", "roundStatus", 1) {

    constructor(round: Round):
            this(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()), round.getRoundId(), round.getGameId(), round.getRoundNumber(), round.getRoundStatus())

    init {
        if (roundStatus != RoundStatus.COMMAND_INPUT_STARTED)
            throw MethodNotAllowedForStatusException("RoundStarted cannot created with round.status = $roundStatus")
    }
}