package microservice.dungeon.game.messaging.producer

import microservice.dungeon.game.aggregates.core.Event
import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

@Component
class KafkaProducer @Autowired constructor (
    private val kafkaTemplate: KafkaTemplate<String, String>
) : KafkaProducing {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun send(event: Event) {
        val record = ProducerRecord<String, String>(event.getTopic(), event.toDTO().serialize())
        record.headers().add(
            RecordHeader("eventId", event.getId().toString().toByteArray())
        )
        record.headers().add(
            RecordHeader("transactionId", event.getTransactionId().toString().toByteArray())
        )
        record.headers().add(
            RecordHeader("version", event.getVersion().toString().toByteArray())
        )
        record.headers().add(
            RecordHeader("timestamp", event.getOccurredAt().getFormattedTime().toByteArray())
        )
        record.headers().add(
            RecordHeader("type", event.getEventName().toByteArray())
        )

        logger.debug("Publishing ${event.getEventName()} to ${event.getTopic()}.")
        logger.trace(eventToString(event))
        kafkaTemplate.send(record)
    }

    override fun send(record: ProducerRecord<String, String>) {
        kafkaTemplate.send(record)
    }

    private fun eventToString(event: Event): String {
        return "Header(eventId:${event.getId()},transactionId:${event.getTransactionId()},version:${event.getVersion()}," +
                "timestamp:${event.getOccurredAt().getFormattedTime()},type:${event.getEventName()})\r\n" +
                "Payload(${event.toDTO().serialize()})"
    }
}