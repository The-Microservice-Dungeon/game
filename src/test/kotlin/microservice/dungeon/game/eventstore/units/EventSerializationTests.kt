package microservice.dungeon.game.eventstore.units

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventBuilder
import microservice.dungeon.game.eventstore.data.DemoEvent

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.time.LocalDateTime
import java.util.*

@EnableKafka
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29092", "port=29092"])
class EventSerializationTests @Autowired constructor(
    private val applicationContext: ApplicationContext,
    private val environment: Environment
) {
    @Test
    fun serializationAndDeserializationTest() {
        val builderSuffix: String = environment.getProperty("eventStore.builderSuffix").toString()
        val event: Event = DemoEvent(UUID.randomUUID(), "testTopic", LocalDateTime.now())
        val serializedEvent: String = event.serialized()

        val demoEventBuilder: Any = applicationContext.getBean("${event.getEventName()}${builderSuffix}")
        assertTrue(demoEventBuilder is EventBuilder)

        if (demoEventBuilder is EventBuilder) {
            val eventDeserialized: Event = demoEventBuilder.deserializedEvent(serializedEvent)
            assertEquals(event.getId(), eventDeserialized.getId())
        }
    }
}