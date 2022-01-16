package microservice.dungeon.game.aggregates.game.events

import microservice.dungeon.game.aggregates.core.AbstractEvent
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.game.events.dto.PlayerStatusEventDto
import java.util.*

class PlayerStatusEvent (
    id: UUID,
    transactionId: UUID,
    occurredAt: EventTime,
    eventName: String,
    topic: String,
    version: Int,

    val playerId: UUID,
    val playerUsername: String

) : AbstractEvent(
    id = id,
    transactionId = transactionId,
    occurredAt = occurredAt,
    eventName = eventName,
    topic = topic,
    version = version
){
    override fun toDTO(): PlayerStatusEventDto = PlayerStatusEventDto(playerId, playerUsername)

    override fun equals(other: Any?): Boolean =
        (other is PlayerStatusEvent)
                && getId() == other.getId()
                && getTransactionId() == other.getTransactionId()
                && getOccurredAt() == other.getOccurredAt()
                && getEventName() == other.getEventName()
                && getTopic() == other.getTopic()
                && getVersion() == other.getVersion()
                && playerId == other.playerId
                && playerUsername == other.playerUsername
}