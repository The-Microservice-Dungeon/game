package microservice.dungeon.game.eventstore

import com.google.common.base.Predicates.instanceOf
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.domain.EventBuilder
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptorStatus
import microservice.dungeon.game.eventstore.data.DemoEvent
import microservice.dungeon.game.eventstore.data.DemoEventBuilder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import java.time.Instant
import java.util.*

@SpringBootTest
class EventSerializationTests @Autowired constructor(
    private val applicationContext: ApplicationContext,
    private val environment: Environment
) {
    @Test
    fun serializationAndDeserializationTest() {
        val builderSuffix: String = environment.getProperty("eventStore.builderSuffix").toString()
        val event: Event = DemoEvent(UUID.randomUUID(), "testTopic", Instant.now())
        val serializedEvent: String = event.serialized()


        val demoEventBuilder: Any = applicationContext.getBean("${event.getEventName()}${builderSuffix}")
        assertTrue(demoEventBuilder is EventBuilder)

        if (demoEventBuilder is EventBuilder) {
            val eventDeserialized: Event = demoEventBuilder.deserializedEvent(serializedEvent)
            assertEquals(event.getId(), eventDeserialized.getId())
        }
    }
}