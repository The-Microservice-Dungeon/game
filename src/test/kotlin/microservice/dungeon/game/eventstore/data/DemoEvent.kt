package microservice.dungeon.game.eventstore.data

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import java.time.Instant
import java.util.*


class DemoEvent constructor(
    private val id: UUID,
    private val topic: String,
    private val occurredAt: Instant
): Event {
    private val eventName: String = "DemoEvent"


    override fun getId(): UUID = id

    override fun getEventName(): String = eventName

    override fun getTopic(): String = topic

    override fun getOccurredAt(): Instant = occurredAt

    override fun serialized(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }
}