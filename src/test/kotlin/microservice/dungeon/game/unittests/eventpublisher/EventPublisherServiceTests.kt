package microservice.dungeon.game.unittests.eventpublisher

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.messaging.producer.KafkaProducing
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

class EventPublisherServiceTests {
    private var kafkaProducingMock: KafkaProducing? = null
    private var eventStoreServiceMock: EventStoreService? = null
    private var eventPublisherService: EventPublisherService? = null

    @BeforeEach
    fun setUp() {
        kafkaProducingMock = mock(KafkaProducing::class.java)
        eventStoreServiceMock = mock(EventStoreService::class.java)
        eventPublisherService = EventPublisherService(kafkaProducingMock!!, eventStoreServiceMock!!)
    }

    @Test
    fun publishEventsTest() {
        val mockEvent = mock(Event::class.java)
        val mockDto = mock(EventDto::class.java)
        whenever(mockEvent.getTopic()).thenReturn("someTopic")
        whenever(mockEvent.toDTO()).thenReturn(mockDto)
        whenever(mockDto.serialize()).thenReturn("{some Json}")
        eventPublisherService!!.publishEvents(listOf(mockEvent))
        verify(kafkaProducingMock!!).send(mockEvent.getTopic(), mockDto.serialize())
    }

    @Test
    fun onSuccessfulPublishTest() {
        val eventId = UUID.randomUUID()
        eventPublisherService!!.onSuccessfulPublish(eventId)
        verify(eventStoreServiceMock!!).markAsPublished(listOf(eventId))
    }
}