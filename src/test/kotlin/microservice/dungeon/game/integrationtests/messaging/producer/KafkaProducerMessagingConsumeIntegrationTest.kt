package microservice.dungeon.game.integrationtests.messaging.producer

import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext

@Disabled
@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29096",
    "kafka.consumer.enable=false"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29096", "port=29096"])
class KafkaProducerMessagingConsumeIntegrationTest @Autowired constructor(

) {
//    @BeforeEach
//    fun initialize() {
//        kafkaConsumerMock.resetMessages()
//    }
//
//    @Test
//    fun kafkaConsumerShouldConsumeMessage() {
//        val round = Round(game = Game(10, 100), roundNumber = 3, roundStatus = RoundStatus.COMMAND_INPUT_STARTED)
//        val roundStarted = RoundStarted(round)
//        val message: String = roundStarted.toDTO().serialize()
//
//        kafkaProducer.send(mockTopic, message)
//        Thread.sleep(1000)
//
//        assertThat(kafkaConsumerMock.getMessages().size)
//            .isEqualTo(1)
//    }
}