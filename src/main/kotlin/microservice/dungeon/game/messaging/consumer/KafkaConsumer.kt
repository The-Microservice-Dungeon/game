package microservice.dungeon.game.messaging.consumer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.eventconsumer.services.EventConsumerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class KafkaConsumer @Autowired constructor(
    private val eventConsumerService: EventConsumerService
) {
    @KafkaListener(topics = ["testTopic"])
    fun consume(message: String) {
        val node: JsonNode = ObjectMapper().readTree(message)
        val id: UUID = UUID.fromString(node.get("id").asText())
        eventConsumerService.consumeMessage(id) {

        }
    }
}