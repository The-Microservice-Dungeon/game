package microservice.dungeon.game.messaging

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(value = ["kafka.enable"], havingValue = "true", matchIfMissing = true)
class KafkaProducer @Autowired constructor (

    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun send(topic: String, payload: String) = kafkaTemplate.send(topic, payload)
}