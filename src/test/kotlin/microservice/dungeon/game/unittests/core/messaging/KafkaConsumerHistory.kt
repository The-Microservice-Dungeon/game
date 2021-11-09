package microservice.dungeon.game.unittests.core.messaging

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumerHistory {
    private var messages: List<String> = emptyList()

    @KafkaListener(topics = ["testTopic"])
    fun consume(message: String) { addMessage(message) }


    private fun addMessage(message: String) { messages = messages + listOf(message) }

    fun getMessages() = messages
}