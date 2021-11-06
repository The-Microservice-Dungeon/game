package microservice.dungeon.game.messaging.consumer

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumer @Autowired constructor(

) {
    @KafkaListener(topics = ["testTopic"])
    fun consume(message: String) { println("KafkaConsumer.consume: Message received $message")}
}