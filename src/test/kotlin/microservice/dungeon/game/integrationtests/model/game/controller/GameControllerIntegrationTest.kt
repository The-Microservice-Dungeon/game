package microservice.dungeon.game.integrationtests.model.game.controller

import microservice.dungeon.game.aggregates.game.controller.GameController
import microservice.dungeon.game.aggregates.game.controller.dto.*
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameNotFoundException
import microservice.dungeon.game.aggregates.game.domain.GameStateException
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.dtos.PlayerJoinGameDto
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.servives.GameService
import microservice.dungeon.game.aggregates.player.domain.PlayerNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.*

class GameControllerIntegrationTest {
    private var mockGameService: GameService? = null
    private var mockGameRepository: GameRepository? = null
    private var gameController: GameController? = null
    private var webTestClient: WebTestClient? = null

    @BeforeEach
    fun setUp() {
        mockGameService = mock()
        mockGameRepository = mock()
        gameController = GameController(mockGameService!!, mockGameRepository!!)
        webTestClient = WebTestClient.bindToController(gameController!!).build()
    }

    @Test
    fun shouldAllowToCreateNewGame() {
        // given
        val maxPlayers = 3
        val maxRounds = 100
        val requestBody = CreateGameRequestDto(maxPlayers, maxRounds)
        val game = Game(maxPlayers, maxRounds)
        val anyTransactionId = UUID.randomUUID()
        whenever(mockGameService!!.createNewGame(maxPlayers, maxRounds))
            .thenReturn(Pair(anyTransactionId, game))

        // when then
        val result = webTestClient!!.post().uri("/games")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<CreateGameResponseDto>()
            .returnResult()
        val responseBody = result.responseBody!!

        // and then
        assertThat(responseBody.gameId)
            .isEqualTo(game.getGameId())

        // and then
        verify(mockGameService!!).createNewGame(maxPlayers, maxRounds)
    }

    @Test
    fun shouldRespondForbiddenWhenNewGameCreationFailed() {
        // given
        val requestBody = CreateGameRequestDto(3,3)
        doThrow(GameStateException("A new Game could not be started, because an active Game already exists."))
            .whenever(mockGameService!!)
            .createNewGame(any(), any())

        // when then
        webTestClient!!.post().uri("/games")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isForbidden

        // and then
        verify(mockGameService!!).createNewGame(requestBody.maxPlayers, requestBody.maxRounds)
    }

    @Test
    fun shouldAllowToStartGame() {
        // given
        val gameId: UUID = UUID.randomUUID()
        val transactionId: UUID = UUID.randomUUID()
        whenever(mockGameService!!.startGame(gameId))
            .thenReturn(transactionId)

        // when then
        webTestClient!!.post().uri("/games/${gameId}/gameCommands/start")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated

        // and then
        verify(mockGameService!!).startGame(gameId)
    }

    @Test
    fun shouldRespondNotFoundWhenGameNotExistsWhileGameStarting() {
        // given
        val gameId: UUID = UUID.randomUUID()
        doThrow(GameNotFoundException("any Message"))
            .whenever(mockGameService!!)
            .startGame(gameId)

        // when then
        webTestClient!!.post().uri("/games/${gameId}/gameCommands/start")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound

        // and then
        verify(mockGameService!!).startGame(gameId)
    }

    @Test
    fun shouldRespondForbiddenWhenActionIsNotAllowedWhileGameStarting() {
        // given
        val gameId: UUID = UUID.randomUUID()
        doThrow(GameStateException("any Message"))
            .whenever(mockGameService!!)
            .startGame(gameId)

        // when then
        webTestClient!!.post().uri("/games/${gameId}/gameCommands/start")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isForbidden

        // and then
        verify(mockGameService!!).startGame(gameId)
    }

    @Test
    fun shouldAllowToEndGame() {
        // given
        val gameId: UUID = UUID.randomUUID()
        val transactionId: UUID = UUID.randomUUID()
        whenever(mockGameService!!.endGame(gameId))
            .thenReturn(transactionId)

        // when then
        webTestClient!!.post().uri("/games/${gameId}/gameCommands/end")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated

        // and then
        verify(mockGameService!!).endGame(gameId)
    }

    @Test
    fun shouldRespondNotFoundWhenGameNotExistsWhileGameEnding() {
        // given
        val gameId: UUID = UUID.randomUUID()
        doThrow(GameNotFoundException("any message"))
            .whenever(mockGameService!!)
            .endGame(gameId)

        // when then
        webTestClient!!.post().uri("/games/${gameId}/gameCommands/end")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound

        // and then
        verify(mockGameService!!).endGame(gameId)
    }

    @Test
    fun shouldRespondForbiddenWhenActionIsNotAllowedWhileGameEnding() {
        // given
        val gameId: UUID = UUID.randomUUID()
        doThrow(GameStateException("any message"))
            .whenever(mockGameService!!)
            .endGame(gameId)

        // when then
        webTestClient!!.post().uri("/games/${gameId}/gameCommands/end")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isForbidden

        // and then
        verify(mockGameService!!).endGame(gameId)
    }

    @Test
    fun shouldAllowPlayerToJoinTheGame() {
        // given
        val gameId: UUID = UUID.randomUUID()
        val playerToken: UUID = UUID.randomUUID()
        val transactionId: UUID = UUID.randomUUID()
        whenever(mockGameService!!.joinGame(playerToken, gameId))
            .thenReturn(transactionId)

        // when then
        val result = webTestClient!!.put().uri("/games/${gameId}/players/${playerToken}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<JoinGameResponseDto>()
            .returnResult()
        val responseBody: JoinGameResponseDto = result.responseBody!!

        // and then
        assertThat(responseBody.transactionId)
            .isEqualTo(transactionId)

        // and then
        verify(mockGameService!!).joinGame(playerToken, gameId)
    }

    @Test
    fun shouldRespondNotFoundWhenGameNotExistsWhileJoiningAGame() {
        // given
        val gameId = UUID.randomUUID()
        val playerToken = UUID.randomUUID()
        doThrow(GameNotFoundException("any message"))
            .whenever(mockGameService!!)
            .joinGame(playerToken, gameId)

        // when
        webTestClient!!.put().uri("/games/${gameId}/players/${playerToken}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound

        // then
        verify(mockGameService!!).joinGame(playerToken, gameId)
    }

    @Test
    fun shouldRespondNotFoundWhenPlayerNotExistsWhileJoiningAGame() {
        // given
        val gameId = UUID.randomUUID()
        val playerToken = UUID.randomUUID()
        doThrow(PlayerNotFoundException("any message"))
            .whenever(mockGameService!!)
            .joinGame(playerToken, gameId)

        // when
        webTestClient!!.put().uri("/games/${gameId}/players/${playerToken}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound

        // then
        verify(mockGameService!!).joinGame(playerToken, gameId)
    }

    @Test
    fun shouldRespondForbiddenWhenActionIsNotAllowedWhileJoiningAGame() {
        // given
        val gameId = UUID.randomUUID()
        val playerToken = UUID.randomUUID()
        doThrow(GameStateException("any message"))
            .whenever(mockGameService!!)
            .joinGame(playerToken, gameId)

        // when
        webTestClient!!.put().uri("/games/${gameId}/players/${playerToken}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isForbidden

        // then
        verify(mockGameService!!).joinGame(playerToken, gameId)
    }

    @Test
    fun shouldAllowToFetchCurrentGameTime() {
        // given
        val game = Game(10, 10)
        game.startGame()
        whenever(mockGameRepository!!.findById(game.getGameId()))
            .thenReturn(Optional.of(game))

        // when
        val result = webTestClient!!.get().uri("/games/${game.getGameId()}/time")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<GameTimeResponseDto>()
            .returnResult()
        val responseBody: GameTimeResponseDto = result.responseBody!!

        // then
        verify(mockGameRepository!!).findById(game.getGameId())
        assertThat(responseBody.gameTime)
            .isNotNull
        assertThat(responseBody.roundCount)
            .isEqualTo(1)
        assertThat(responseBody.roundTime)
            .isNotNull
    }

    @Test
    fun shouldRespondNotFoundWhenGameNotExistsWhileFetchingGameTime() {
        // given
        val anyGameId = UUID.randomUUID()

        // when
        webTestClient!!.get().uri("/games/${anyGameId}/time")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound

        // then
        verify(mockGameRepository!!).findById(anyGameId)
    }

    @Test
    fun shouldAllowToRetrieveAllActiveGames() {
        // given
        val game = Game(10, 10)
        whenever(mockGameRepository!!.findAllByGameStatusIn(listOf(GameStatus.CREATED, GameStatus.GAME_RUNNING)))
            .thenReturn(listOf(game))

        // when
        val result = webTestClient!!.get().uri("/games")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<List<GameResponseDto>>()
            .returnResult()
        val responseBodies: List<GameResponseDto> = result.responseBody!!

        // then
        assertThat(responseBodies)
            .hasSize(1)
        assertThat(responseBodies[0])
            .isEqualTo(GameResponseDto(game))
    }
}