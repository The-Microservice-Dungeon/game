package microservice.dungeon.game.eventstore.units

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptorStatus
import microservice.dungeon.game.aggregates.eventstore.repositories.EventDescriptorRepository
import microservice.dungeon.game.eventstore.data.DemoEvent
import microservice.dungeon.game.eventstore.data.compareEventDescriptors
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import javax.transaction.Transactional

@EnableKafka
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29092", "port=29092"])
class EventDescriptorPersistenceTests @Autowired constructor(
    private val applicationContext: ApplicationContext,
    private val environment: Environment,
    private val eventDescriptorRepository: EventDescriptorRepository
) {
    @BeforeEach
    fun resetDatabase() {
        eventDescriptorRepository.deleteAll()
    }

    @Test
    fun eventDescriptorPersistenceTest() {
        val originalEvent: Event = DemoEvent(UUID.randomUUID(), "testTopic", LocalDateTime.now())
        val eventDescriptor: EventDescriptor = EventDescriptor(originalEvent)
        assertEquals(originalEvent.getOccurredAt(), eventDescriptor.getOccurredAt())

        eventDescriptorRepository.save(eventDescriptor)
        val unpublishedEventDescriptors: List<EventDescriptor> = eventDescriptorRepository.findByStatus(EventDescriptorStatus.CREATED)
        assertEquals(unpublishedEventDescriptors.size, 1)
        assertEquals(eventDescriptor.getOccurredAt(), unpublishedEventDescriptors[0].getOccurredAt())
        assertEquals(compareEventDescriptors(eventDescriptor, unpublishedEventDescriptors[0]), 0)
    }

    @Test
    @Transactional
    fun eventDescriptorMarkAsPublishedTest() {
        val originalEvent: Event = DemoEvent(UUID.randomUUID(), "testTopic", LocalDateTime.now())
        val eventDescriptor: EventDescriptor = EventDescriptor(originalEvent)
        eventDescriptorRepository.save(eventDescriptor)

        eventDescriptorRepository.markAsPublished(listOf(originalEvent.getId()))
        val unpublishedEventDescriptors: List<EventDescriptor> = eventDescriptorRepository.findByStatus(EventDescriptorStatus.CREATED)
        val publishedEventDescriptors: List<EventDescriptor> = eventDescriptorRepository.findByStatus(EventDescriptorStatus.PUBLISHED)
        assertEquals(unpublishedEventDescriptors.size, 0)
        assertEquals(publishedEventDescriptors.size, 1)
    }

    @Test
    @Transactional
    fun eventDescriptorDeletePublishedTest() {
        val originalEvent: Event = DemoEvent(UUID.randomUUID(), "testTopic", LocalDateTime.now())
        val eventDescriptor: EventDescriptor = EventDescriptor(originalEvent)
        eventDescriptorRepository.save(eventDescriptor)

        eventDescriptorRepository.deletePublished(listOf(originalEvent.getId()))
        val unpublishedEventDescriptors: List<EventDescriptor> = eventDescriptorRepository.findByStatus(EventDescriptorStatus.CREATED)
        val publishedEventDescriptors: List<EventDescriptor> = eventDescriptorRepository.findByStatus(EventDescriptorStatus.PUBLISHED)
        assertEquals(unpublishedEventDescriptors.size, 0)
        assertEquals(publishedEventDescriptors.size, 0)
    }
}