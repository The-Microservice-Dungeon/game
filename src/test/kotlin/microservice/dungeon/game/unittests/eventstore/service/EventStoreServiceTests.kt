package microservice.dungeon.game.unittests.eventstore.service

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptorStatus
import microservice.dungeon.game.aggregates.eventstore.repositories.EventDescriptorRepository
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.aggregates.round.events.RoundStartedBuilder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.*
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

    private var someValidEvent: Event? = null


    @BeforeEach
    fun setUp() {
        eventDescriptorRepositoryMock = mock(EventDescriptorRepository::class.java)
        applicationContextMock = mock(ApplicationContext::class.java)
        environmentMock = mock(Environment::class.java)
        eventStoreService = EventStoreService(eventDescriptorRepositoryMock!!, applicationContextMock!!, environmentMock!!, publishingMode)

        someValidEvent = RoundStarted(UUID.randomUUID(), LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
    }


    @Test
    fun storeEventShouldSaveEventDescriptor() {
        val eventDescriptor = EventDescriptor(someValidEvent!!)
        whenever(eventDescriptorRepositoryMock!!.findById(any()))
            .thenReturn(Optional.empty())

        eventStoreService!!.storeEvent(someValidEvent!!)

        argumentCaptor<EventDescriptor>().apply {
            verify(eventDescriptorRepositoryMock!!).save(capture())
            assertTrue(firstValue.isSameAs(eventDescriptor))
        }
    }

    @Test
    fun storeEventShouldThrowWhenEventAlreadyExists() {
        val mockEventDescriptor: EventDescriptor = mock()
        whenever(eventDescriptorRepositoryMock!!.findById(someValidEvent!!.getId()))
            .thenReturn(Optional.of(mockEventDescriptor))

        assertThatThrownBy {
            eventStoreService!!.storeEvent(someValidEvent!!)
        }
    }


    @Test
    fun markAsPublishedShouldUpdateWhenModeIsUpdate() {
        val listOfValidIds = listOf(UUID.randomUUID())
        val publishMode = "UPDATE"
        eventStoreService = EventStoreService(eventDescriptorRepositoryMock!!, applicationContextMock!!, environmentMock!!, publishMode)

        eventStoreService!!.markAsPublished(listOfValidIds)

        verify(eventDescriptorRepositoryMock!!).markAsPublished(listOfValidIds)
    }

    @Test
    fun markAsPublishedShouldDeleteWhenModeIsDelete() {
        val listOfValidIds = listOf(UUID.randomUUID())
        val publishMode = "DELETE"
        eventStoreService = EventStoreService(eventDescriptorRepositoryMock!!, applicationContextMock!!, environmentMock!!, publishMode)

        eventStoreService!!.markAsPublished(listOfValidIds)

        verify(eventDescriptorRepositoryMock!!).deletePublished(listOfValidIds)
    }

    @Test
    fun markAsPublishedShouldThrowWhenModeIsInvalid() {
        val listOfValidIds = listOf(UUID.randomUUID())
        val invalidPublishMode = ""

        eventStoreService = EventStoreService(eventDescriptorRepositoryMock!!, applicationContextMock!!, environmentMock!!, invalidPublishMode)

        assertThatThrownBy {
            eventStoreService!!.markAsPublished(listOfValidIds)
        }
    }


    @Test
    fun fetchUnpublishedEventsShouldLoadEventDescriptors() {
        eventStoreService!!.fetchUnpublishedEvents()
        verify(eventDescriptorRepositoryMock!!).findByStatus(EventDescriptorStatus.CREATED)
    }

    @Test
    fun fetchUnpublishedEventsShouldMapToEventsWhenLoaded() {
        val validEventDescriptor = EventDescriptor(someValidEvent!!)
        whenever(eventDescriptorRepositoryMock!!.findByStatus(EventDescriptorStatus.CREATED))
            .thenReturn(listOf(validEventDescriptor))
        whenever(environmentMock!!.getProperty("eventStore.builderSuffix")).thenReturn("Builder")
        whenever(applicationContextMock!!.getBean("roundStartedBuilder")).thenReturn(RoundStartedBuilder())

        val listOfEvents = eventStoreService!!.fetchUnpublishedEvents()

        assertThat(listOfEvents)
            .hasSize(1)
            .anyMatch { event ->
                event.isSameAs(someValidEvent!!)
            }
    }

}