package microservice.dungeon.game.eventstore.mockbeans

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.eventpublisher.mockbeans.DemoEventDto
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
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

    override fun toDTO(): EventDto {
        return DemoEventDto(id, topic, occurredAt)
    }
}