package microservice.dungeon.game.unittests.eventstore.service

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.InvalidApplicationPropertyException
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptorStatus
import microservice.dungeon.game.aggregates.eventstore.repositories.EventDescriptorRepository
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import java.time.LocalDateTime
import java.util.*

class EventStoreServiceTests {
    private var eventDescriptorRepositoryMock: EventDescriptorRepository? = null
    private var applicationContextMock: ApplicationContext? = null
    private var environmentMock: Environment? = null
    private val publishingMode: String = "UPDATING"
    private var eventStoreService: EventStoreService? = null

    @BeforeEach
    fun setUp() {
        eventDescriptorRepositoryMock = mock(EventDescriptorRepository::class.java)
        applicationContextMock = mock(ApplicationContext::class.java)
        environmentMock = mock(Environment::class.java)
        eventStoreService = EventStoreService(eventDescriptorRepositoryMock!!, applicationContextMock!!, environmentMock!!, publishingMode)
    }

    @Test
    fun storeEventTest() {
        val event = RoundStarted(UUID.randomUUID(), LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
        eventStoreService!!.storeEvent(event)

        verify(eventDescriptorRepositoryMock!!).save(argThat { eventDescriptor ->
            eventDescriptor.getId() == event.getId() &&
            eventDescriptor.getStatus() == EventDescriptorStatus.CREATED
        })
    }

    @Test
    fun markAsPublishedWithUpdateTest() {
        val ids = listOf(UUID.randomUUID())
        eventStoreService = EventStoreService(eventDescriptorRepositoryMock!!, applicationContextMock!!, environmentMock!!, "UPDATE")

        eventStoreService!!.markAsPublished(ids)
        verify(eventDescriptorRepositoryMock!!).markAsPublished(ids)
    }

    @Test
    fun markAsPublishedWithDeleteTest() {
        val ids = listOf(UUID.randomUUID())
        eventStoreService = EventStoreService(eventDescriptorRepositoryMock!!, applicationContextMock!!, environmentMock!!, "DELETE")

        eventStoreService!!.markAsPublished(ids)
        verify(eventDescriptorRepositoryMock!!).deletePublished(ids)
    }

    @Test
    fun markAsPublishedInvalidConfigTest() {
        val ids = listOf(UUID.randomUUID())
        eventStoreService = EventStoreService(eventDescriptorRepositoryMock!!, applicationContextMock!!, environmentMock!!, "")

        assertThrows(InvalidApplicationPropertyException::class.java) {
            eventStoreService!!.markAsPublished(ids)
        }
    }

    @Test
    fun fetchUnpublishedEventsTest() {
        eventStoreService!!.fetchUnpublishedEvents()
        verify(eventDescriptorRepositoryMock!!).findByStatus(EventDescriptorStatus.CREATED)
    }
}