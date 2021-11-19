package microservice.dungeon.game.integrationtests.messaging.mockbeans

import org.springframework.context.annotation.Scope
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
@Scope("singleton")
class KafkaConsumerMock {
    private var messages: List<String> = emptyList()

    @KafkaListener(topics = ["\${kafka.topicMock}"])
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