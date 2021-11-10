package microservice.dungeon.game.unittests.messaging.producer

import microservice.dungeon.game.messaging.producer.KafkaProducerListener
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.context.ApplicationContext

class KafkaProducerListenerTests {
    private var applicationContextMock: ApplicationContext? = null
    private var kafkaProducerListener: KafkaProducerListener<String, String>? = null

    @BeforeEach
    fun initialize() {
        applicationContextMock = mock(ApplicationContext::class.java)
        kafkaProducerListener = KafkaProducerListener(applicationContextMock!!)
    }

    @Test
    fun onSuccessCorrectMessageTest() {
        //TODO
    }

    @Test
    fun onSuccessFaultyMessageTest() {
        //TODO
    }

    @Test
    fun onErrorTest() {

    }
}