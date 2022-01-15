package microservice.dungeon.game.integrationtests.model.game.controller

import microservice.dungeon.game.aggregates.game.controller.GameController
import microservice.dungeon.game.aggregates.game.controller.dto.CreateGameRequestDto
import microservice.dungeon.game.aggregates.game.controller.dto.CreateGameResponseDto
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStateException
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.servives.GameService
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

        // when
        val result = webTestClient!!.post().uri("/games")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<CreateGameResponseDto>()
            .returnResult()
        val responseBody = result.responseBody!!

        // then
        assertThat(responseBody.gameId)
            .isEqualTo(game.getGameId())
    }

    @Test
    fun shouldRespondForbiddenWhenNewGameCreationFailed() {
        // given
        val requestBody = CreateGameRequestDto(3,3)
        doThrow(GameStateException("A new Game could not be started, because an active Game already exists."))
            .whenever(mockGameService!!)
            .createNewGame(any(), any())

        // when
        webTestClient!!.post().uri("/games")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isForbidden
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
    fun shouldRespondForbiddenWhenGameStartingFailed() {
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
}