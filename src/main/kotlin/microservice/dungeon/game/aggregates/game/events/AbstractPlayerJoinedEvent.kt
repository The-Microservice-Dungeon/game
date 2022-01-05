package microservice.dungeon.game.aggregates.game.events

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.dtos.GameEventDto
import microservice.dungeon.game.aggregates.game.dtos.PlayerJoinedEventDto
import java.time.LocalDateTime
import java.util.*

abstract class AbstractPlayerJoinedEvent(
    private val id: UUID,
    private val occurredAt: EventTime,
    private val gameId: UUID,
    private val playerId: UUID,
    private val eventName: String,
    private val topic: String,
    private val version: Int
): Event  {

    override fun getId(): UUID = id
    override fun getOccurredAt(): EventTime = occurredAt

    override fun getTransactionId(): UUID = gameId

    fun getGameId(): UUID = gameId

    fun getPlayerId(): UUID = playerId

    override fun getEventName(): String = eventName

    override fun getVersion(): Int = version

    override fun getTopic(): String = topic

    override fun serialized(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }

    override fun toDTO(): EventDto {
        return PlayerJoinedEventDto(playerId)
    }

    override fun equals(other: Any?): Boolean =
        (other is AbstractPlayerJoinedEvent)
                && id == other.id
                && occurredAt == other.occurredAt
                && gameId == other.gameId
                && playerId == other.playerId
                && eventName == other.eventName
                && topic == other.topic
                && version == other.version

    override fun isSameAs(event: Event): Boolean =
        getId() == event.getId()
                && getTransactionId() == event.getTransactionId()
                && getOccurredAt() == event.getOccurredAt()
                && getEventName() == event.getEventName()
                && getTopic() == event.getTopic()
                && getVersion() == event.getVersion()
}



