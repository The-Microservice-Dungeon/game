package microservice.dungeon.game.integrationtests.eventconsumer

import microservice.dungeon.game.aggregates.eventconsumer.domain.EventMessage
import microservice.dungeon.game.aggregates.eventconsumer.repositories.EventMessageRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29099"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29099", "port=29099"])
class EventConsumerPersistenceTests @Autowired constructor(
    private val eventMessageRepository: EventMessageRepository,
    private val transactionTemplate: TransactionTemplate
) {
    @BeforeEach
    fun initialize() {
        eventMessageRepository.deleteAll()
    }

    @Test
    fun saveEventMessageAndFindTest() {
        val eventMessage = EventMessage(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now())
        val eventMessageId = transactionTemplate. execute {
            eventMessageRepository.save(eventMessage)
            eventMessage.getId()
        }!!
        val loadedEventMessage = transactionTemplate.execute {
            eventMessageRepository.findById(eventMessageId).get()
        }!!
        assertTrue(eventMessage.equalsValue(loadedEventMessage))
    }
}