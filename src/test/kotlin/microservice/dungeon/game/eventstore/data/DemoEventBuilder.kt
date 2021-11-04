package microservice.dungeon.game.eventstore.data

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.domain.EventBuilder
import org.springframework.stereotype.Component

@Component(value = "DemoEvent-Builder")
class DemoEventBuilder: EventBuilder {
    override fun deserializedEvent(serialized: String): Event {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.readValue(serialized, DemoEvent::class.java)
    }
}