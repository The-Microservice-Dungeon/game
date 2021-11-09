package microservice.dungeon.game.aggregates.round.events

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.dtos.RoundEventDto
import java.time.LocalDateTime
import java.util.*

class CommandInputEnded (
    id: UUID,
    occurredAt: LocalDateTime,
    roundId: UUID,
    gameId: UUID,
    roundNumber: Int,
    roundStatus: RoundStatus
) : AbstractRoundEvent(id, occurredAt, roundId, gameId, roundNumber, roundStatus, "commandInputEnded", "testTopic") {

    constructor(round: Round):
            this(UUID.randomUUID(), LocalDateTime.now(), round.getRoundId(), round.getGameId(), round.getRoundNumber(), round.getRoundStatus())

    init {
        if (roundStatus != RoundStatus.COMMAND_INPUT_ENDED)
            throw MethodNotAllowedForStatusException("RoundStarted cannot created with round.status = $roundStatus")
    }
}