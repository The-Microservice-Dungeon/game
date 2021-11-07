package microservice.dungeon.game.eventstore.servicetests

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptorStatus
import microservice.dungeon.game.aggregates.eventstore.repositories.EventDescriptorRepository
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.eventpublisher.mockbeans.DemoEvent
import org.junit.jupiter.api.Assertions.assertEquals
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
class OutboxingEventsTest @Autowired constructor(
    private val eventDescriptorRepository: EventDescriptorRepository,
    private val eventStoreService: EventStoreService,
    private val eventPublisherService: EventPublisherService
) {
    @BeforeEach
    fun resetDatabase() {
        eventDescriptorRepository.deleteAll()
    }

    @Test
    fun publishEventsSuccessfullyWithEventListenerFeedbackTest() {
        val events: List<Event> = listOf(DemoEvent(UUID.randomUUID(), "testTopic", LocalDateTime.now()))
        eventStoreService.storeEvent(events[0])
        eventPublisherService.publishEvents(events)
        Thread.sleep(1000);

        val unpublishedEvents: List<EventDescriptor> = eventDescriptorRepository.findByStatus(EventDescriptorStatus.CREATED)
        val publishedEvents: List<EventDescriptor> = eventDescriptorRepository.findByStatus(EventDescriptorStatus.PUBLISHED)
        assertEquals(unpublishedEvents.size, 0)
        assertEquals(publishedEvents.size, 1)
    }
}