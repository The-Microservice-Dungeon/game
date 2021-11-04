package microservice.dungeon.game.eventstore

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.repositories.EventDescriptorRepository
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.eventstore.data.DemoEvent
import microservice.dungeon.game.eventstore.data.compareEvents
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import java.time.Instant
import java.util.*

@SpringBootTest
class EventStoreServiceTests @Autowired constructor(
    private val eventDescriptorRepository: EventDescriptorRepository,
    private val eventStoreService: EventStoreService
) {
    @BeforeEach
    fun resetDatabase() {
        eventDescriptorRepository.deleteAll()
    }

    @Test
    fun saveEventLoadEventMarkAsPublishedTest() {
        val originalEvent: Event = DemoEvent(UUID.randomUUID(), "testTopic", Instant.now())
        eventStoreService.storeEvent(originalEvent)

        val unpublishedEvents: List<Event> = eventStoreService.fetchUnpublishedEvents()
        val unpublishedEvent: Event = unpublishedEvents[0]

        assertEquals(unpublishedEvents.size, 1)
        assertTrue(compareEvents(originalEvent, unpublishedEvent))

        eventStoreService.markAsPublished(listOf(originalEvent).map{ x -> x.getId()})
        val finalUnpublishedEvents: List<Event> = eventStoreService.fetchUnpublishedEvents()

        assertEquals(finalUnpublishedEvents.size, 0)
    }
}