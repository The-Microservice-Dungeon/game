package microservice.dungeon.game.unittests.eventstore.mockbeans

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventBuilder
import org.springframework.stereotype.Component

@Component
class DemoEventBuilder: EventBuilder {
    override fun deserializedEvent(serialized: String): Event {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.readValue(serialized, DemoEvent::class.java)
    }
}