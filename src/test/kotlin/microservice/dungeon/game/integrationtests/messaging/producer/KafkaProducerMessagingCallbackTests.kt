package microservice.dungeon.game.integrationtests.messaging.producer

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.messaging.consumer.KafkaRobotConsumer
import microservice.dungeon.game.messaging.producer.KafkaProducer
import microservice.dungeon.game.messaging.producer.KafkaProducerListener
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Scope
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.lang.Thread.sleep
import java.util.*

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29092", "port=29092"])
class KafkaProducerMessagingCallbackTests @Autowired constructor(
    private val kafkaProducer: KafkaProducer,
    private val kafkaProducerListener: KafkaProducerListener<String, String>,
    private val kafkaRobotConsumer: KafkaRobotConsumer
) {
    @BeforeEach
    fun initialize() {
        reset(kafkaProducerListener)
    }

    @Test
    fun sendMessageSuccessfullyAndValidateCallbackTest() {
        val round = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)
        val roundStarted = RoundStarted(round)
        val message: String = roundStarted.toDTO().serialize()
        kafkaProducer.send("testTopic", message)
        sleep(1000)
        verify(kafkaProducerListener).onSuccess(argThat { producerRecord ->
            producerRecord.value() == message
        }, any())
    }

    @Test
    fun sendMessageUnsuccessfullyTest() {

    }

    @TestConfiguration
    class KafkaProducerMessagingTestsConfig {
        @Bean
        @Primary
        @Scope("singleton")
        fun <K,V>kafkaProducerListenerMock(): KafkaProducerListener<K, V> = mock()
    }
}