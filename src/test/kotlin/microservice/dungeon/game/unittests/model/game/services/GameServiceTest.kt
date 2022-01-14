package microservice.dungeon.game.unittests.model.game.services

import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStateException
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.servives.GameService
import microservice.dungeon.game.aggregates.game.web.MapGameWorldsClient
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

class GameServiceTest {
    private var mockGameRepository: GameRepository? = null
    private var mockRoundService: RoundService? = null
    private var mockPlayerRepository: PlayerRepository? = null
    private var mockEventStoreService: EventStoreService? = null
    private var mockEventPublisherService: EventPublisherService? = null
    private var mockMapGameWorldsClient: MapGameWorldsClient? = null

    private var gameService: GameService? = null

    @BeforeEach
    fun setUp() {
        mockGameRepository = mock()
        mockRoundService = mock()
        mockPlayerRepository = mock()
        mockEventStoreService = mock()
        mockEventPublisherService = mock()
        mockMapGameWorldsClient = mock()

        gameService = GameService(
            mockRoundService!!,
            mockGameRepository!!,
            mockPlayerRepository!!,
            mockEventStoreService!!,
            mockEventPublisherService!!,
            mockMapGameWorldsClient!!
        )
    }

    @Test
    fun shouldAllowToCreateANewGame() {
        // given
        val maxNumberOfPlayers = 10
        val maxNumberOfRounds = 100

        // when
        val response: Pair<UUID, Game> = gameService!!.createNewGame(maxNumberOfPlayers, maxNumberOfRounds)

        // then
        val game = response.second
        assertThat(game.getGameStatus())
            .isEqualTo(GameStatus.CREATED)
        assertThat(game.getMaxPlayers())
            .isEqualTo(maxNumberOfPlayers)
        assertThat(game.getMaxRounds())
            .isEqualTo(maxNumberOfRounds)

        // and then
        verify(mockGameRepository!!).save(game)
    }

    @Test
    fun shouldPreventGameCreationWhenActiveGameAlreadyExists() {
        // given
        whenever(mockGameRepository!!.existsByGameStatusIn(listOf(GameStatus.CREATED, GameStatus.GAME_RUNNING)))
            .thenReturn(true)

        // when
        assertThrows(GameStateException::class.java) {
            gameService!!.createNewGame(10, 100)
        }
    }

    @Test
    fun shouldPublishWhenGameCreated() {
        assertTrue(false)
    }
}