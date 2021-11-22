package microservice.dungeon.game.aggregates.player.events

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.player.dtos.PlayerEventDto
import java.util.*

abstract class AbstractPlayerEvent (
    private val id: UUID,
    private val transactionId: UUID,
    private val occurredAt: EventTime,
    private val eventName: String,
    private val topic: String,
    private val version: Int,
    private val playerId: UUID,
    private val userName: String,
    private val mailAddress: String
): Event {
    override fun getId(): UUID = id

    override fun getTransactionId(): UUID = transactionId

    override fun getOccurredAt(): EventTime = occurredAt

    override fun getEventName(): String = eventName

    override fun getTopic(): String = topic

    override fun getVersion(): Int = version

    fun getPlayerId(): UUID = playerId

    fun getUserName(): String = userName

    fun getMailAddress(): String = mailAddress

    override fun serialized(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }

    override fun toDTO(): EventDto = PlayerEventDto(playerId, userName, mailAddress)

    override fun equals(other: Any?): Boolean =
        (other is AbstractPlayerEvent)
                && id == other.id
                && transactionId == other.transactionId
                && occurredAt == other.occurredAt
                && eventName == other.eventName
                && topic == other.topic
                && version == other.version
                && playerId == other.playerId
                && userName == other.userName
                && mailAddress == other.mailAddress

    override fun isSameAs(event: Event): Boolean =
        getId() == event.getId()
                && getTransactionId() == event.getTransactionId()
                && getOccurredAt() == event.getOccurredAt()
                && getEventName() == event.getEventName()
                && getTopic() == event.getTopic()
                && getVersion() == event.getVersion()
}