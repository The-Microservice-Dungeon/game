package microservice.dungeon.game.unittests.eventpublisher

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.messaging.producer.KafkaProducing
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.*

class EventPublisherServiceTest {
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
    fun shouldAllowToPublishListOfEvents() {
        // given
        val mockEvent: Event = mock()
        val validListOfEvents = listOf(mockEvent, mockEvent)

        // when
        eventPublisherService!!.publishEvents(validListOfEvents)

        // then
        verify(kafkaProducingMock!!, times(2)).send(mockEvent)
    }

    @Test
    fun shouldAllowToPublishEvent() {
        // given
        val mockEvent: Event = mock()

        // when
        eventPublisherService!!.publishEvent(mockEvent)

        // then
        verify(kafkaProducingMock!!).send(mockEvent)
    }

    @Test
    fun shouldAllowToUpdateEventsAsSuccessfullyPublished() {
        // given
        val eventId = UUID.randomUUID()

        // when
        eventPublisherService!!.onSuccessfulPublish(eventId)

        // then
        verify(eventStoreServiceMock!!).markAsPublished(listOf(eventId))
    }
}
