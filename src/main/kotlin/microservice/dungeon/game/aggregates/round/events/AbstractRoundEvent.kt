package microservice.dungeon.game.aggregates.round.events

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.dtos.RoundEventDto
import java.time.LocalDateTime
import java.util.*

abstract class AbstractRoundEvent constructor(
    private val id: UUID,
    private val occurredAt: EventTime,
    private val roundId: UUID,
    private val gameId: UUID,
    private val roundNumber: Int,
    private val roundStatus: RoundStatus,
    private val eventName: String,
    private val topic: String,
    private val version: Int
): Event  {

    override fun getId(): UUID = id

    override fun getTransactionId(): UUID = roundId

    override fun getOccurredAt(): EventTime = occurredAt

    fun getRoundId(): UUID = roundId

    fun getGameId(): UUID = gameId

    fun getRoundNumber(): Int = roundNumber

    fun getRoundStatus(): RoundStatus = roundStatus

    override fun getEventName(): String = eventName

    override fun getTopic(): String = topic

    override fun getVersion(): Int = version

    override fun serialized(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }

    override fun toDTO(): EventDto {
        return RoundEventDto(roundId, roundNumber, roundStatus)
    }

    override fun equals(other: Any?): Boolean =
        (other is AbstractRoundEvent)
                && id == other.id
                && occurredAt == other.occurredAt
                && roundId == other.roundId
                && gameId == other.gameId
                && roundNumber == other.roundNumber
                && roundStatus == other.roundStatus
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