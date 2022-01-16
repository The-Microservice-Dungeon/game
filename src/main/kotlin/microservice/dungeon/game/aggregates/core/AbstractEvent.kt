package microservice.dungeon.game.aggregates.core

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import java.util.*

abstract class AbstractEvent (
    private val id: UUID,
    private val transactionId: UUID,
    private val occurredAt: EventTime,
    private val eventName: String,
    private val topic: String,
    private val version: Int

) : Event {

    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }

    override fun getId(): UUID = id

    override fun getTransactionId(): UUID = transactionId

    override fun getOccurredAt(): EventTime = occurredAt

    override fun getEventName(): String = eventName

    override fun getTopic(): String = topic

    override fun getVersion(): Int = version

    override fun serialized(): String = objectMapper.writeValueAsString(this)

    override fun isSameAs(event: Event): Boolean =
        getId() == event.getId()
                && getTransactionId() == event.getTransactionId()
                && getOccurredAt() == event.getOccurredAt()
                && getEventName() == event.getEventName()
                && getTopic() == event.getTopic()
                && getVersion() == event.getVersion()
}