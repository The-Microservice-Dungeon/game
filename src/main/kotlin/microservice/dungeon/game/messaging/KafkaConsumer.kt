package microservice.dungeon.game.messaging

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(value = ["kafka.enable"], havingValue = "true", matchIfMissing = true)
class KafkaConsumer {

    @KafkaListener(topics = ["testTopic"])
    fun consume(message: String) = println("Consumed message: $message @testTopic")
}