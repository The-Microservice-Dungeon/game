package microservice.dungeon.game.eventstore.servicetests

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.repositories.EventDescriptorRepository
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.eventstore.mockbeans.DemoEvent
import microservice.dungeon.game.eventstore.mockbeans.compareEvents
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.time.LocalDateTime
import java.util.*

@EnableKafka
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29092", "port=29092"])
class StoringEventsTest @Autowired constructor(
    private val eventDescriptorRepository: EventDescriptorRepository,
    private val eventStoreService: EventStoreService
) {
    @BeforeEach
    fun resetDatabase() {
        eventDescriptorRepository.deleteAll()
    }

    @Test
    fun saveEventLoadEventMarkAsPublishedTest() {
        val originalEvent: Event = DemoEvent(UUID.randomUUID(), "testTopic", LocalDateTime.now())
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