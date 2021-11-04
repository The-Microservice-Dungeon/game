package microservice.dungeon.game.eventstore

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptorStatus
import microservice.dungeon.game.eventstore.data.DemoEvent
import microservice.dungeon.game.eventstore.data.compareEventDescriptorWithEvent
import microservice.dungeon.game.eventstore.data.compareEvents
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import java.time.Instant
import java.util.*

@SpringBootTest
class EventDescriptorTests @Autowired constructor(
    private val applicationContext: ApplicationContext,
    private val environment: Environment
) {
    @Test
    fun eventDescriptorFromEventTest() {
        val event: Event = DemoEvent(UUID.randomUUID(), "testTopic", Instant.now())
        val eventDescriptor: EventDescriptor = EventDescriptor(event)

        assertTrue(compareEventDescriptorWithEvent(eventDescriptor, event))
    }

    @Test
    fun eventRebuildingFromDescriptorTest() {
        val originalEvent: Event = DemoEvent(UUID.randomUUID(), "testTopic", Instant.now())
        val eventDescriptor: EventDescriptor = EventDescriptor(originalEvent)

        val event: Event = eventDescriptor.getAsEvent(environment, applicationContext)
        assertTrue(compareEvents(originalEvent, event))
    }
}