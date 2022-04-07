package microservice.dungeon.game.unittests.model.game.services

import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameNotFoundException
import microservice.dungeon.game.aggregates.game.domain.GameStateException
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.events.GameStatusEvent
import microservice.dungeon.game.aggregates.game.events.GameStatusEventBuilder
import microservice.dungeon.game.aggregates.game.events.PlayerStatusEvent
import microservice.dungeon.game.aggregates.game.events.PlayerStatusEventBuilder
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.services.GameService
import microservice.dungeon.game.aggregates.game.web.MapGameWorldsClient
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.domain.PlayerNotFoundException
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.round.events.RoundStatusEventBuilder
import microservice.dungeon.game.aggregates.round.services.RoundService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.*

class GameServiceTest {
    private var mockGameRepository: GameRepository? = null
    private var mockRoundService: RoundService? = null
    private var mockPlayerRepository: PlayerRepository? = null
    private var mockEventStoreService: EventStoreService? = null
    private var mockEventPublisherService: EventPublisherService? = null
    private var mockMapGameWorldsClient: MapGameWorldsClient? = null

    private val gameStatusEventBuilder = GameStatusEventBuilder("anyTopic", "anyType", 1)
    private val playerStatusEventBuilder = PlayerStatusEventBuilder("anyTopic", "anyType", 1)
    private val roundStatusEventBuilder = RoundStatusEventBuilder("anyTopic", "anyType", 1)

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
            mockMapGameWorldsClient!!,
            gameStatusEventBuilder,
            playerStatusEventBuilder,
            roundStatusEventBuilder
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

        // when then
        assertThrows(GameStateException::class.java) {
            gameService!!.createNewGame(10, 100)
        }
    }

    @Test
    fun shouldPublishWhenGameCreated() {
        // given
        // when
        val response: Pair<UUID, Game> = gameService!!.createNewGame(1,1)

        // then
        verify(mockEventStoreService!!).storeEvent(check { event: GameStatusEvent ->
            assertThat(event.getTransactionId())
                .isEqualTo(response.first)
            assertThat(event.gameId)
                .isEqualTo(response.second.getGameId())
            assertThat(event.gameStatus)
                .isEqualTo(GameStatus.CREATED)
        })
        verify(mockEventPublisherService!!).publishEvent(check { event: GameStatusEvent ->
            assertThat(event.getTransactionId())
                .isEqualTo(response.first)
            assertThat(event.gameId)
                .isEqualTo(response.second.getGameId())
            assertThat(event.gameStatus)
                .isEqualTo(GameStatus.CREATED)
        })
    }

    @Test
    fun shouldAllowPlayerToJoinAGame() {
        // given
        val spyGame: Game = spy(Game(2, 100))
        val player: Player = Player("dadepu", "some mail")

        whenever(mockGameRepository!!.findById(spyGame.getGameId()))
            .thenReturn(Optional.of(spyGame))
        whenever(mockPlayerRepository!!.findByPlayerToken(player.getPlayerToken()))
            .thenReturn(Optional.of(player))

        // when
        val transactionId: UUID = gameService!!.joinGame(player.getPlayerToken(), spyGame.getGameId())

        // then
        verify(spyGame).joinGame(player)

        // and then
        verify(mockGameRepository!!).save(spyGame)
    }

    @Test
    fun shouldThrowPlayerNotFoundExceptionWhenPlayerNotFoundWhileJoiningAGame() {
        // given
        val game: Game = Game(10, 100)
        val anyPlayerToken = UUID.randomUUID()

        whenever(mockGameRepository!!.findById(any()))
            .thenReturn(Optional.of(game))

        // when
        assertThrows(PlayerNotFoundException::class.java) {
            gameService!!.joinGame(anyPlayerToken, game.getGameId())
        }
    }

    @Test
    fun shouldThrowGameNotFoundExceptionWhenGameNotFoundWhileJoiningAGame() {
        // given
        val anyGameId = UUID.randomUUID()
        val player = Player("dadepu", "any mail")

        whenever(mockPlayerRepository!!.findByPlayerToken(any()))
            .thenReturn(Optional.of(player))

        // when
        assertThrows(GameNotFoundException::class.java) {
            gameService!!.joinGame(player.getPlayerToken(), anyGameId)
        }
    }

    @Test
    fun shouldPublishPlayerJoinedGameOnSuccess() {
        // given
        val game = Game(1,1)
        val player = Player("dadepu", "dadepu@smail.th-koeln.de")
        whenever(mockGameRepository!!.findById(game.getGameId()))
            .thenReturn(Optional.of(game))
        whenever(mockPlayerRepository!!.findByPlayerToken(player.getPlayerToken()))
            .thenReturn(Optional.of(player))

        // when
        val transactionId: UUID = gameService!!.joinGame(player.getPlayerToken(), game.getGameId())

        // then
        verify(mockEventStoreService!!).storeEvent(check { event: PlayerStatusEvent ->
            assertThat(event.getTransactionId())
                .isEqualTo(transactionId)
            assertThat(event.playerId)
                .isEqualTo(player.getPlayerId())
            assertThat(event.playerUsername)
                .isEqualTo(player.getUserName())
        })
        verify(mockEventPublisherService!!).publishEvent(check { event: PlayerStatusEvent ->
            assertThat(event.getTransactionId())
                .isEqualTo(transactionId)
            assertThat(event.playerId)
                .isEqualTo(player.getPlayerId())
            assertThat(event.playerUsername)
                .isEqualTo(player.getUserName())
        })
    }

    @Test
    fun shouldAllowToStartTheGame() {
        // given
        val spyGame = spy(Game(10, 100))

        whenever(mockGameRepository!!.findById(spyGame.getGameId()))
            .thenReturn(Optional.of(spyGame))

        // when
        val transactionId: UUID = gameService!!.startGame(spyGame.getGameId())

        // then
        verify(spyGame).startGame()
        verify(mockGameRepository!!).save(spyGame)

        // and then
        verify(mockMapGameWorldsClient!!).createNewGameWorld(spyGame.getNumberJoinedPlayers())
    }

    @Test
    fun shouldPublishGameStartedOnSuccess() {
        // given
        val game = Game(1,1)
        whenever(mockGameRepository!!.findById(game.getGameId()))
            .thenReturn(Optional.of(game))

        // when
        val transactionId: UUID = gameService!!.startGame(game.getGameId())

        // then
        verify(mockEventStoreService!!).storeEvent(check { event: GameStatusEvent ->
            assertThat(event.getTransactionId())
                .isEqualTo(transactionId)
            assertThat(event.gameId)
                .isEqualTo(game.getGameId())
            assertThat(event.gameStatus)
                .isEqualTo(GameStatus.GAME_RUNNING)
        })
        verify(mockEventPublisherService!!).publishEvent(check { event: GameStatusEvent ->
            assertThat(event.getTransactionId())
                .isEqualTo(transactionId)
            assertThat(event.gameId)
                .isEqualTo(game.getGameId())
            assertThat(event.gameStatus)
                .isEqualTo(GameStatus.GAME_RUNNING)
        })
    }

    @Test
    fun shouldThrowGameNotFoundExceptionWhenGameNotFoundWhileStarting() {
        // given
        val anyGameId = UUID.randomUUID()

        // when
        assertThrows(GameNotFoundException::class.java) {
            gameService!!.startGame(anyGameId)
        }
    }

    @Test
    fun shouldAllowToEndGame() {
        // given
        val spyGame = spy(Game(10, 100))
        spyGame.startGame()

        whenever(mockGameRepository!!.findById(spyGame.getGameId()))
            .thenReturn(Optional.of(spyGame))

        // when
        val transactionId = gameService!!.endGame(spyGame.getGameId())

        // then
        verify(spyGame).endGame()
        verify(mockGameRepository!!).save(spyGame)
    }

    @Test
    fun shouldThrowGameNotFoundExceptionWhenGameNotFoundWhileEnding() {
        // given
        val anyGameId = UUID.randomUUID()

        // when
        assertThrows(GameNotFoundException::class.java) {
            gameService!!.endGame(anyGameId)
        }
    }
}
