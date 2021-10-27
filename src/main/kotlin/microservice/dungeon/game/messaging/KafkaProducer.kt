package microservice.dungeon.game.messaging

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer @Autowired constructor (

    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun send(topic: String, payload: String) = kafkaTemplate.send(topic, payload)
}