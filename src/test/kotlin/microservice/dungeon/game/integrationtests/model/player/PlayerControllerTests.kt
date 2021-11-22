package microservice.dungeon.game.integrationtests.model.player

import microservice.dungeon.game.aggregates.player.controller.PlayerController
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.dtos.PlayerResponseDto
import microservice.dungeon.game.aggregates.player.services.PlayerService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestTemplate
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.body
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "kafka.bootstrapAddress=localhost:29098"
])
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29098", "port=29098"])
class PlayerControllerTests {
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
    fun contextLoads() {
    }

    @Test
    fun shouldAllowNewPlayerCreation() {
        // given
        val requestEntity = PlayerResponseDto(null, "SOME_NAME", "SOME_MAIL")
        whenever(mockPlayerService!!.createNewPlayer(anyString(), anyString()))
            .thenReturn(Player(requestEntity.name, requestEntity.email))

        // when
        val result = webTestClient!!.post().uri("/players")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestEntity)
            .exchange()
            .expectStatus().isCreated
            .expectBody<PlayerResponseDto>()
            .returnResult()
        val responseBody = result.responseBody!!

        // then
        assertThat(responseBody.bearerToken)
            .isNotNull
        assertThat(responseBody.name)
            .isEqualTo(requestEntity.name)
        assertThat(responseBody.email)
            .isEqualTo(requestEntity.email)
        verify(mockPlayerService!!).createNewPlayer(requestEntity.name, requestEntity.email)
    }

    // valid interaction
    // incorrect data
}