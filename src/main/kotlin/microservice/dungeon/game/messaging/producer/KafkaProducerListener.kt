package microservice.dungeon.game.messaging.producer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.kafka.support.ProducerListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class KafkaProducerListener<K, V> @Autowired constructor(
    private val applicationContext: ApplicationContext

) : ProducerListener<K, V> {

    override fun onSuccess(producerRecord: ProducerRecord<K, V>, recordMetadata: RecordMetadata) {
        val message: V = producerRecord.value()
        if (message is String) {
            val node: JsonNode = ObjectMapper().readTree(message)
            val id: UUID = UUID.fromString(node.get("id").asText())

            val eventPublisherService: Any = applicationContext.getBean("eventPublisherService")
            if (eventPublisherService is EventPublisherService) {
                eventPublisherService.onSuccessfulPublish(id)
            }
        }
    }
}