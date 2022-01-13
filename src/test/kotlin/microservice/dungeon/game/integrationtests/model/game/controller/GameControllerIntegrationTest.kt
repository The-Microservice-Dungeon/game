package microservice.dungeon.game.integrationtests.model.game.controller

import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.controller.GameController
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.dtos.PlayerJoinGameDto
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.servives.GameService
import microservice.dungeon.game.aggregates.game.web.MapGameWorldsClient
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.*

//class GameControllerIntegrationTest {
//    private var mockPlayerRepository: PlayerRepository? = null
//    private var mockGameRepository: GameRepository? = null
//    private var mockRoundService: RoundService? = null
//    private var mockEventStoreService: EventStoreService? = null
//    private var mockEventPublisherService: EventPublisherService? = null
//    private var mockMapGameWorldsClient: MapGameWorldsClient? = null
//
//    private var gameService: GameService? = null
//    private var gameController: GameController? = null
//
//    private var webTestClient: WebTestClient? = null
//
//    private var ANY_GAME = Game(maxRounds = 100, maxPlayers = 10)
//    private var ANY_PLAYER = Player("dadepu", "dadepu@th-koeln.de")
//
//    @BeforeEach
//    fun setUp() {
//        mockPlayerRepository = mock()
//        mockGameRepository = mock()
//        mockRoundService = mock()
//        mockEventStoreService = mock()
//        mockEventPublisherService = mock()
//        mockMapGameWorldsClient = mock()
//        gameService = GameService(
//            mockRoundService!!,
//            mockGameRepository!!,
//            mockPlayerRepository!!,
//            mockEventStoreService!!,
//            mockEventPublisherService!!,
//            mockMapGameWorldsClient!!
//        )
//        gameController = GameController(gameService!!)
//        webTestClient = WebTestClient.bindToController(gameController!!).build()
//    }
//
//    @Test
//    fun shouldReturnTransactionIdWhenPlayerJoinsGame() {
//        // given
//        whenever(mockPlayerRepository!!.findByPlayerToken(ANY_PLAYER.getPlayerToken()))
//            .thenReturn(Optional.of(ANY_PLAYER))
//        whenever(mockGameRepository!!.findByGameId(ANY_GAME.getGameId()))
//            .thenReturn(Optional.of(ANY_GAME))
//
//        // when
//        val uri = "/games/${ANY_GAME.getGameId()}/players/${ANY_PLAYER.getPlayerId()}"
//        val result = webTestClient!!.put().uri(uri)
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isCreated
//            .expectBody<PlayerJoinGameDto>()
//            .returnResult()
//        val responseBody = result.responseBody
//    }
//}