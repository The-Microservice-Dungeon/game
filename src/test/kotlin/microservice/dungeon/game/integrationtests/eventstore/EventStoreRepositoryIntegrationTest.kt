package microservice.dungeon.game.integrationtests.eventstore

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptorStatus
import microservice.dungeon.game.aggregates.eventstore.repositories.EventDescriptorRepository
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29094"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29094", "port=29094"])
class EventStoreRepositoryIntegrationTest @Autowired constructor(
    private val eventDescriptorRepository: EventDescriptorRepository,
    private val transactionTemplate: TransactionTemplate,
) {
//    @BeforeEach
//    fun initialize() {
//        eventDescriptorRepository.deleteAll()
//    }
//
//    @Test
//    fun saveEventDescriptorAndFindTest() {
//        val event: Event = RoundStarted(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
//        val eventDescriptor = EventDescriptor(event)
//        transactionTemplate.execute {
//            eventDescriptorRepository.save(eventDescriptor)
//        }
//        val loadedDescriptor = transactionTemplate.execute {
//            eventDescriptorRepository.findById(eventDescriptor.getId()).get()
//        }!!
//        assertEquals(loadedDescriptor.getId(), eventDescriptor.getId())
//    }
//
//    @Test
//    fun markAsPublishedPublishedEventDescriptorsTest() {
//        val event: Event = RoundStarted(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
//        val eventDescriptor = EventDescriptor(event)
//        transactionTemplate.execute {
//            eventDescriptorRepository.save(eventDescriptor)
//        }
//        transactionTemplate.execute {
//            eventDescriptorRepository.markAsPublished(listOf(eventDescriptor.getId()))
//        }
//        val loadedDescriptor = transactionTemplate.execute {
//            eventDescriptorRepository.findById(eventDescriptor.getId()).get()
//        }!!
//        assertEquals(loadedDescriptor.getStatus(), EventDescriptorStatus.PUBLISHED)
//    }
//
//    @Test
//    fun deletePublishedEventDescriptorsTest() {
//        val event: Event = RoundStarted(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
//        val eventDescriptor = EventDescriptor(event)
//        transactionTemplate.execute {
//            eventDescriptorRepository.save(eventDescriptor)
//        }
//        val event2: Event = RoundStarted(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
//        val eventDescriptor2 = EventDescriptor(event2)
//        transactionTemplate.execute {
//            eventDescriptorRepository.save(eventDescriptor2)
//        }
//        transactionTemplate.execute {
//            eventDescriptorRepository.deletePublished(listOf(eventDescriptor2.getId()))
//        }
//        val loadedDescriptors = transactionTemplate.execute {
//            eventDescriptorRepository.findAll()
//        }!!.toList()
//        assertEquals(loadedDescriptors.size, 1)
//        assertEquals(loadedDescriptors.first().getId(), eventDescriptor.getId())
//    }
//
//    @Test
//    fun saveEventDescriptorAndMarkPublishedAndLoadAllByStatusTest() {
//        val event: Event = RoundStarted(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
//        val eventDescriptor = EventDescriptor(event)
//        transactionTemplate.execute {
//            eventDescriptorRepository.save(eventDescriptor)
//        }
//        val event2: Event = RoundStarted(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
//        val eventDescriptor2 = EventDescriptor(event2)
//        transactionTemplate.execute {
//            eventDescriptorRepository.save(eventDescriptor2)
//            eventDescriptorRepository.markAsPublished(listOf(eventDescriptor2.getId()))
//        }
//        val loadedDescriptors = transactionTemplate.execute {
//            eventDescriptorRepository.findByStatus(EventDescriptorStatus.CREATED)
//        }!!
//        assertEquals(loadedDescriptors.size, 1)
//    }
}