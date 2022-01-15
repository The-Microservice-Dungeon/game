package microservice.dungeon.game.integrationtests.model.game.services

import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.servives.GameService
import microservice.dungeon.game.aggregates.game.web.MapGameWorldsClient
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29096"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29096", "port=29096"])
class GameServiceIntegrationTest @Autowired constructor(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository
) {
    private var mockRoundService: RoundService? = null
    private var mockGameWorldsClient: MapGameWorldsClient? = null
    private var mockEventStoreService: EventStoreService? = null
    private var mockEventPublisherService: EventPublisherService? = null

    private var gameService: GameService? = null

    @BeforeEach
    fun setUp() {
        gameRepository.deleteAll()
        playerRepository.deleteAll()

        mockRoundService = mock()
        mockGameWorldsClient = mock()
        mockEventPublisherService = mock()
        mockEventStoreService = mock()
        gameService = GameService(
            mockRoundService!!,
            gameRepository,
            playerRepository,
            mockEventStoreService!!,
            mockEventPublisherService!!,
            mockGameWorldsClient!!
        )
    }

    /**
     *      IF Game is set to FINISHED and if the maximum number of rounds exists, it is safe to assume,
     *      that the GameLoop is running in parallel.
     */
    @Test
    fun shouldRunGameLoopInParallelWhenGameIsStarted() {
        // given
        val game: Game = Game(2,2)
        game.setTotalRoundTimespanInMS(200)
        gameRepository.save(game)

        // when
        gameService!!.startGame(game.getGameId())
        Thread.sleep(2000)

        // then
        val capturedGame: Game = gameRepository.findById(game.getGameId()).get()
        assertThat(capturedGame.getGameStatus())
            .isEqualTo(GameStatus.GAME_FINISHED)

        // and then
        val currentRound: Round = capturedGame.getCurrentRound()!!
        assertThat(currentRound.getRoundNumber())
            .isEqualTo(2)


    }
}