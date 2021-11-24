package microservice.dungeon.game.contracttests.model.player.controller

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.player.controller.PlayerController
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.dtos.PlayerResponseDto
import microservice.dungeon.game.aggregates.player.services.PlayerService
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "kafka.bootstrapAddress=localhost:29102"
    ])
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29102", "port=29102"])
class PlayerControllerPostCreateNewPlayerContractTest {
    private var mockPlayerService: PlayerService? = null
    private var playerController: PlayerController? = null
    private var webTestClient: WebTestClient? = null


    @BeforeEach
    fun setUp() {
        mockPlayerService = mock()
        playerController = PlayerController(mockPlayerService!!)
        webTestClient = WebTestClient.bindToController(playerController!!).build()
    }


    @Test
    fun shouldCreateNewPlayer() {
        // given
        val requestEntity = PlayerResponseDto(null, "SOME_NAME", "SOME_MAIL")
        val mockPlayerNewlyCreated = Player(requestEntity.name, requestEntity.email)
        whenever(mockPlayerService!!.createNewPlayer(anyString(), anyString()))
            .thenReturn(mockPlayerNewlyCreated)

        // when then
        val result = webTestClient!!.post().uri("/players")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestEntity)
            .exchange()
            .expectStatus().isEqualTo(201)
            .expectBody<PlayerResponseDto>()
            .returnResult()
        val responseBody = result.responseBody!!

        // and
        assertThat(responseBody.bearerToken)
            .isNotNull
        assertThat(responseBody.name)
            .isEqualTo(requestEntity.name)
        assertThat(responseBody.email)
            .isEqualTo(requestEntity.email)
    }

    @Test
    fun shouldFailToCreateNewPlayerWhenPlayerWithSameMailOrUsernameAlreadyExists() {
        // given
        val requestEntity = PlayerResponseDto(null, "SOME_NAME", "SOME_MAIL")
        whenever(mockPlayerService!!.createNewPlayer(anyString(), anyString()))
            .doAnswer{ throw EntityAlreadyExistsException("Player already exists") }

        // when then
        webTestClient!!.post().uri("/players")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestEntity)
            .exchange()
            .expectStatus().isEqualTo(406)
    }

    @Test
    fun shouldAbortWhenInputBodyInvalid() {

    }
}