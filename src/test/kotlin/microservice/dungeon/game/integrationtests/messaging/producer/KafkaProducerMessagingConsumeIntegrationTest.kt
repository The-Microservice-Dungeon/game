package microservice.dungeon.game.integrationtests.messaging.producer

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.integrationtests.messaging.mockbeans.KafkaConsumerMock
import microservice.dungeon.game.messaging.producer.KafkaProducer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Scope
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.util.*

@Disabled
@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29096",
    "kafka.consumer.enable=false"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29096", "port=29096"])
class KafkaProducerMessagingConsumeIntegrationTest @Autowired constructor(
    private val kafkaProducer: KafkaProducer,
    private val kafkaConsumerMock: KafkaConsumerMock,
    @Value(value = "\${kafka.topicMock}")
    private val mockTopic: String
) {
    @BeforeEach
    fun initialize() {
        kafkaConsumerMock.resetMessages()
    }

    @Test
    fun kafkaConsumerShouldConsumeMessage() {
        val round = Round(game = Game(10, 100), roundNumber = 3, roundStatus = RoundStatus.COMMAND_INPUT_STARTED)
        val roundStarted = RoundStarted(round)
        val message: String = roundStarted.toDTO().serialize()

        kafkaProducer.send(mockTopic, message)
        Thread.sleep(1000)

        assertThat(kafkaConsumerMock.getMessages().size)
            .isEqualTo(1)
    }
}