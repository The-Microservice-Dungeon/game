package microservice.dungeon.game.unittests.messaging.producer

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.messaging.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.kafka.core.KafkaTemplate
import java.time.LocalDateTime
import java.util.*

class KafkaProducerTest {
    private var kafkaTemplateMock: KafkaTemplate<String, String>? = null
    private var kafkaProducer: KafkaProducer? = null

    private var validEvent: Event? = null

//    @BeforeEach
//    fun setUp() {
//        kafkaTemplateMock = mock()
//        kafkaProducer = KafkaProducer(kafkaTemplateMock!!)
//
//        validEvent = RoundStarted(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
//    }
//
//    @Test
//    fun sendShouldPublishValidProducerRecord() {
//        kafkaProducer!!.send(validEvent!!)
//
//        val recordCaptor = argumentCaptor<ProducerRecord<String, String>>()
//        verify(kafkaTemplateMock!!).send(recordCaptor.capture())
//        val capturedRecord = recordCaptor.firstValue
//
//        assertThat(capturedRecord)
//            .containsTopic(validEvent!!)
//        assertThat(capturedRecord)
//            .containsHeader(validEvent!!)
//        assertThat(capturedRecord)
//            .containsPayload(validEvent!!)
//    }
}