package microservice.dungeon.game.integrationtests.eventpublisher

import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.events.GameStatusEvent
import microservice.dungeon.game.aggregates.game.events.GameStatusEventBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.check
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29093"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29093", "port=29093"])
class EventPublisherIntegrationTest @Autowired constructor(
    private val eventPublisherService: EventPublisherService,
    private val eventStoreService: EventStoreService,
    private val gameStatusEventBuilder: GameStatusEventBuilder
) {

    @Test
    fun publishEventsShouldSendMessageAndReceiveCallbackWhenPublishedSuccessfully() {
        // given
        val event: GameStatusEvent = gameStatusEventBuilder.makeGameStatusEvent(UUID.randomUUID(), UUID.randomUUID(), GameStatus.CREATED)

        // when
        eventPublisherService.publishEvent(event)
        Thread.sleep(200)

        // then
        verify(eventStoreService).markAsPublished(check { eventIds: List<UUID> ->
            assertThat(eventIds)
                .hasSize(1)
            assertThat(eventIds)
                .contains(event.getId())
        })
    }

    @TestConfiguration
    class EventPublisherIntegrationTestConfig {
        @Bean
        @Primary
        fun eventStoreServiceMock(): EventStoreService {
            return mock(EventStoreService::class.java)
        }
    }
}