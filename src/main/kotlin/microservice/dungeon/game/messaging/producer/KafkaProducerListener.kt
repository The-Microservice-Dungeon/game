package microservice.dungeon.game.messaging.producer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.header.Header
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.kafka.support.ProducerListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class KafkaProducerListener<K, V> @Autowired constructor(
    private val applicationContext: ApplicationContext,
    @Value("\${kafka.message.format.header.eventId}")
    private val messageHeaderEventIdKey: String

) : ProducerListener<K, V> {

    override fun onSuccess(producerRecord: ProducerRecord<K, V>, recordMetadata: RecordMetadata?) {
        val header: Header = producerRecord.headers().headers(messageHeaderEventIdKey).first()
        val eventId = UUID.fromString(String(header.value()))

        val eventPublisherService: EventPublisherService = applicationContext.getBean("eventPublisherService") as EventPublisherService
        eventPublisherService.onSuccessfulPublish(eventId)
    }
}