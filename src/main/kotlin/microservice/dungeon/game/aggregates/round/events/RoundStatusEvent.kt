package microservice.dungeon.game.aggregates.round.events

import microservice.dungeon.game.aggregates.core.AbstractEvent
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.dto.RoundStatusEventDto
import java.util.*

class RoundStatusEvent (
    id: UUID,
    transactionId: UUID,
    occurredAt: EventTime,
    eventName: String,
    topic: String,
    version: Int,

    val roundId: UUID,
    val roundNumber: Int,
    val roundStatus: RoundStatus

) : AbstractEvent(
    id = id,
    transactionId = transactionId,
    occurredAt = occurredAt,
    eventName = eventName,
    topic = topic,
    version = version
) {
    override fun toDTO(): RoundStatusEventDto = RoundStatusEventDto(roundId, roundNumber, roundStatus)

    override fun equals(other: Any?): Boolean =
        (other is RoundStatusEvent)
                && getId() == other.getId()
                && getTransactionId() == other.getTransactionId()
                && getOccurredAt() == other.getOccurredAt()
                && getEventName() == other.getEventName()
                && getTopic() == other.getTopic()
                && getVersion() == other.getVersion()
                && roundId == other.roundId
                && roundNumber == other.roundNumber
                && roundStatus == other.roundStatus
}