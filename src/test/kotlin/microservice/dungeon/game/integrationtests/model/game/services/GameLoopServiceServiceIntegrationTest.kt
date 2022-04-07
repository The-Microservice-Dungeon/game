package microservice.dungeon.game.integrationtests.model.game.services

import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.events.GameStatusEventBuilder
import microservice.dungeon.game.aggregates.game.events.PlayerStatusEventBuilder
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.services.GameService
import microservice.dungeon.game.aggregates.game.web.MapGameWorldsClient
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.events.RoundStatusEventBuilder
import microservice.dungeon.game.aggregates.round.services.RoundService
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29097"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29097", "port=29097"])
class GameLoopServiceServiceIntegrationTest @Autowired constructor(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val gameStatusEventBuilder: GameStatusEventBuilder,
    private val roundStatusEventBuilder: RoundStatusEventBuilder,
    private val playerStatusEventBuilder: PlayerStatusEventBuilder
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
            mockGameWorldsClient!!,
            gameStatusEventBuilder,
            playerStatusEventBuilder,
            roundStatusEventBuilder
        )
    }



    /**
     *      IF Game is set to FINISHED and if the maximum number of rounds exists, it is safe to assume,
     *      that the GameLoop is running in parallel while main-thread is sleeping.
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
        Assertions.assertThat(capturedGame.getGameStatus())
            .isEqualTo(GameStatus.GAME_FINISHED)

        // and then
        val currentRound: Round = capturedGame.getCurrentRound()!!
        Assertions.assertThat(currentRound.getRoundNumber())
            .isEqualTo(2)
    }

    @Test
    fun shouldExitEarlyWhenGameEndsBeforeMaxNumberOfRoundsReached() {
        // given
        val game: Game = Game(1, 100)
        game.setTotalRoundTimespanInMS(100)
        gameRepository.save(game)

        // when
        gameService!!.startGame(game.getGameId())
        Thread.sleep(2000)
        gameService!!.endGame(game.getGameId())
        Thread.sleep(250)

        // then
        val capturedGame: Game = gameRepository.findById(game.getGameId()).get()
        val currentRound: Round = capturedGame.getCurrentRound()!!
        assertThat(currentRound.getRoundNumber())
            .isBetween(3, 100)
    }
}
