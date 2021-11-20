package microservice.dungeon.game.assertions

import microservice.dungeon.game.aggregates.core.Event
import org.apache.kafka.clients.producer.ProducerRecord
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat
import java.time.format.DateTimeFormatter
import java.util.*

class ProducerRecordAssertion(actual: ProducerRecord<String, String>):
    AbstractObjectAssert<ProducerRecordAssertion, ProducerRecord<String, String>>(actual, ProducerRecordAssertion::class.java){

    fun containsHeader(event: Event): ProducerRecordAssertion {
        val eventId =       UUID.fromString(String(actual.headers().headers("eventId").first().value()))
        val transactionId = UUID.fromString(String(actual.headers().headers("transactionId").first().value()))
        val version =       String(actual.headers().headers("version").first().value()).toInt()
        val timestamp =     String(actual.headers().headers("timestamp").first().value())
        val type =          String(actual.headers().headers("type").first().value())

        assertThat(eventId)
            .isEqualTo(event.getId())
        assertThat(transactionId)
            .isEqualTo(event.getTransactionId())
        assertThat(version)
            .isEqualTo(event.getVersion())
        assertThat(timestamp)
            .isEqualTo(event.getOccurredAt().getFormattedTime())
        assertThat(type)
            .isEqualTo(event.getEventName())
        return this
    }

    fun containsTopic(event: Event): ProducerRecordAssertion {
        assertThat(actual.topic())
            .isEqualTo(event.getTopic())
        return this
    }

    fun containsPayload(event: Event): ProducerRecordAssertion {
        assertThat(actual.value())
            .isEqualTo(event.toDTO().serialize())
        return this
    }
}