package microservice.dungeon.game.messaging

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumer {

    @KafkaListener(topics = ["testTopic"])
    fun consume(message: String) = println("Consumed message: $message @testTopic")
}