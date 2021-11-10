package microservice.dungeon.game.integrationtests.eventpublisher

import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.kafka.support.ProducerListener
import org.springframework.kafka.test.context.EmbeddedKafka
import java.lang.Thread.sleep
import java.util.*

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29092", "port=29092"])
class EventPublisherLiveTests @Autowired constructor(
    private val eventPublisherService: EventPublisherService,
    private val eventStoreService: EventStoreService
) {

    @Test
    fun sendMessageSuccessfullyAndValidateCallbackTest() {
        val round = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)
        val roundStarted = RoundStarted(round)
        eventPublisherService.publishEvents(listOf(roundStarted))

        sleep(1000)
        verify(eventStoreService).markAsPublished(listOf(roundStarted.getId()))
    }

    @TestConfiguration
    class EventPublisherLiveTestsConfig {
        @Bean
        @Primary
        fun eventStoreServiceMock(): EventStoreService {
            return mock(EventStoreService::class.java)
        }
    }
}