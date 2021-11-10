package microservice.dungeon.game.integrationtests.messaging.mockbeans

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumerMock {
    private var messages: List<String> = emptyList()

    @KafkaListener(topics = ["testTopic"])
    fun consume(message: String) {
        messages = messages + listOf(message)
    }

    fun getMessages(): List<String> {
        return messages
    }

    fun resetMessages() {
        messages = listOf()
    }
}