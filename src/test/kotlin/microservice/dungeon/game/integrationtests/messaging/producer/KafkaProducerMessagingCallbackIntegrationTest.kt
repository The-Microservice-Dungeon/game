package microservice.dungeon.game.integrationtests.messaging.producer

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
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
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.lang.Thread.sleep
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29095"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29095", "port=29095"])
class KafkaProducerMessagingCallbackIntegrationTest @Autowired constructor(
    private val kafkaProducer: KafkaProducer,
    private val kafkaProducerListener: KafkaProducerListener<String, String>
) {
    private var round: Round? = null
    private var roundStarted: RoundStarted? = null

    @BeforeEach
    fun initialize() {
        round = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)
        roundStarted = RoundStarted(round!!)
        reset(kafkaProducerListener)
    }

    @Test
    fun sendMessageShouldTriggerCallbackWhenSendSuccessfully() {
        kafkaProducer.send("testTopic", roundStarted!!.toDTO().serialize())
        sleep(1000)

        verify(kafkaProducerListener).onSuccess(argThat { producerRecord ->
            producerRecord.value() == roundStarted!!.toDTO().serialize()
        }, any())
    }

    @TestConfiguration
    class KafkaProducerMessagingTestsConfig {
        @Bean
        @Primary
        @Scope("singleton")
        fun <K,V>kafkaProducerListenerMock(): KafkaProducerListener<K, V> = mock()
    }
}