package microservice.dungeon.game.eventpublisher.usecases

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.core.messaging.KafkaConsumerHistory
import microservice.dungeon.game.eventpublisher.data.DemoEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.lang.Thread.sleep
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@EnableKafka
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29092", "port=29092"])
class EventPublishingTest @Autowired constructor(
    private val eventPublisherService: EventPublisherService,
    private val kafkaConsumerHistory: KafkaConsumerHistory
) {
    @Test
    fun publishAndReceiveEventTest() {
        val event = DemoEvent(UUID.randomUUID(), "testTopic", LocalDateTime.now())
        val events: List<Event> = listOf(event)
        eventPublisherService.publishEvents(events)
        sleep(1000);

        val receivedMessages: List<String> = kafkaConsumerHistory.getMessages()
        assertEquals(receivedMessages.size, 1)
        assertEquals(receivedMessages.first(), event.serialized())
    }
}