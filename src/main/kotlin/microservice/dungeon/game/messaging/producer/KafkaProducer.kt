package microservice.dungeon.game.messaging.producer

import microservice.dungeon.game.aggregates.core.Event
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

    override fun send(topic: String, payload: String) {
        kafkaTemplate.send(topic, payload)
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
        kafkaTemplate.send(record)
    }

    override fun send(record: ProducerRecord<String, String>) {
        kafkaTemplate.send(record)
    }
}