package microservice.dungeon.game.eventpublisher.data

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import java.time.Instant
import java.time.LocalDateTime
import java.util.*


class DemoEvent constructor(
    private val id: UUID,
    private val topic: String,
    private val occurredAt: LocalDateTime
): Event {
    private val eventName: String = "demoEvent"


    override fun getId(): UUID = id

    override fun getEventName(): String = eventName

    override fun getTopic(): String = topic

    override fun getOccurredAt(): LocalDateTime = occurredAt

    override fun serialized(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }

    override fun serializedMessage(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }
}