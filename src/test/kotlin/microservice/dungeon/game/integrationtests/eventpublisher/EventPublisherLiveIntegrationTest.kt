package microservice.dungeon.game.integrationtests.eventpublisher

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.lang.Thread.sleep
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29093"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29093", "port=29093"])
class EventPublisherLiveIntegrationTest @Autowired constructor(
    private val eventPublisherService: EventPublisherService,
    private val eventStoreService: EventStoreService
) {
    private var game: Game? = null
    private var round: Round? = null
    private var roundStarted: Event? = null

//    @BeforeEach
//    fun setUp() {
//        game = Game(10, 100)
//        round = Round(game = game!!, roundNumber = 3, roundStatus = RoundStatus.COMMAND_INPUT_STARTED)
//        roundStarted = RoundStarted(round!!)
//    }
//
//    @Test
//    fun publishEventsShouldSendMessageAndReceiveCallbackWhenPublishedSuccessfully() {
//        eventPublisherService.publishEvents(listOf(roundStarted!!))
//
//        sleep(1000)
//        verify(eventStoreService).markAsPublished(listOf(roundStarted!!.getId()))
//    }
//
//    @TestConfiguration
//    class EventPublisherLiveTestsConfig {
//        @Bean
//        @Primary
//        fun eventStoreServiceMock(): EventStoreService {
//            return mock(EventStoreService::class.java)
//        }
//    }
}