package microservice.dungeon.game.integrationtests.messaging.producer

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.integrationtests.messaging.mockbeans.KafkaConsumerMock
import microservice.dungeon.game.messaging.producer.KafkaProducer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Scope
import org.springframework.kafka.test.context.EmbeddedKafka
import java.util.*

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29092", "port=29092"])
class KafkaProducerMessagingConsumeTests @Autowired constructor(
    private val kafkaProducer: KafkaProducer,
    private val kafkaConsumerMock: KafkaConsumerMock
) {
    @BeforeEach
    fun initialize() {
        kafkaConsumerMock.resetMessages()
    }

    @Test
    fun sendMessageSuccessfullyAndConsumeTest() {
        val round = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)
        val roundStarted = RoundStarted(round)
        val message: String = roundStarted.toDTO().serialize()

        kafkaProducer.send("testTopic", message)
        Thread.sleep(1000)

        assertEquals(kafkaConsumerMock.getMessages().size, 1)
        println(kafkaConsumerMock.getMessages().first())
    }

    @TestConfiguration
    class KafkaProducerMessagingConsumeTestsConfig {
        @Bean
        @Primary
        @Scope("singleton")
        fun kafkaConsumerMockSingleton(): KafkaConsumerMock = KafkaConsumerMock()
    }
}