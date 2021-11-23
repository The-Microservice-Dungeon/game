package microservice.dungeon.game.unittests.messaging.producer

import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.messaging.producer.KafkaProducerListener
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationContext
import java.util.*

class KafkaProducerListenerTests {
    private var eventPublisherServiceMock: EventPublisherService? = null
    private var applicationContextMock: ApplicationContext? = null
    private var kafkaProducerListener: KafkaProducerListener<String, String>? = null

    private val KAFKA_MESSAGE_HEADER_EVENTID = "eventId"

    @BeforeEach
    fun setUp() {
        eventPublisherServiceMock = mock()
        applicationContextMock = mock()
        whenever(applicationContextMock!!.getBean("eventPublisherService"))
            .thenReturn(eventPublisherServiceMock!!)
        kafkaProducerListener = KafkaProducerListener(applicationContextMock!!, KAFKA_MESSAGE_HEADER_EVENTID)
    }

    @Test
    fun onSuccessShouldNotifyEventPublisherWithEventIdWhenProducerRecordIsValid() {
        val validEventId = UUID.randomUUID()
        val validProducerRecord = ProducerRecord<String, String>("someTopic", "someContent")
        validProducerRecord.headers().add(RecordHeader(KAFKA_MESSAGE_HEADER_EVENTID, validEventId.toString().toByteArray()))

        kafkaProducerListener!!.onSuccess(validProducerRecord, null)

        verify(eventPublisherServiceMock!!).onSuccessfulPublish(validEventId)
    }
}