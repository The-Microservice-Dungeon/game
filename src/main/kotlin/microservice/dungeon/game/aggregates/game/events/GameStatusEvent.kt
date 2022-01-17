package microservice.dungeon.game.aggregates.game.events

import microservice.dungeon.game.aggregates.core.AbstractEvent
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.events.dto.GameStatusEventDto
import java.util.*

class GameStatusEvent (
    id: UUID,
    transactionId: UUID,
    occurredAt: EventTime,
    eventName: String,
    topic: String,
    version: Int,

    val gameId: UUID,
    val gameStatus: GameStatus

) : AbstractEvent(
    id = id,
    transactionId = transactionId,
    occurredAt = occurredAt,
    eventName = eventName,
    topic = topic,
    version = version
) {
    override fun toDTO(): GameStatusEventDto = GameStatusEventDto(gameId, gameStatus)

    override fun equals(other: Any?): Boolean =
        (other is GameStatusEvent)
                && getId() == other.getId()
                && getTransactionId() == other.getTransactionId()
                && getOccurredAt() == other.getOccurredAt()
                && getEventName() == other.getEventName()
                && getTopic() == other.getTopic()
                && getVersion() == other.getVersion()
                && gameId == other.gameId
                && gameStatus == other.gameStatus
}