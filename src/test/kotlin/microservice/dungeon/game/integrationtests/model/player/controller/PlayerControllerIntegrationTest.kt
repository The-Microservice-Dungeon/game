package microservice.dungeon.game.integrationtests.model.player.controller

import microservice.dungeon.game.aggregates.player.controller.PlayerController
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.controller.dtos.PlayerResponseDto
import microservice.dungeon.game.aggregates.player.domain.PlayerAlreadyExistsException
import microservice.dungeon.game.aggregates.player.services.PlayerService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

class PlayerControllerIntegrationTest {
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
    fun shouldAllowToCreateNewPlayer() {
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

    @Test
    fun shouldRespondForbiddenWhenPlayerAlreadyExistsWhileCreatingNew() {
        // given
        val requestEntity = PlayerResponseDto(null, "SOME_NAME", "SOME_MAIL")
        doThrow(PlayerAlreadyExistsException::class)
            .whenever(mockPlayerService!!)
            .createNewPlayer(any(), any())

        // when
        webTestClient!!.post().uri("/players")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestEntity)
            .exchange()
            .expectStatus().isForbidden

        // then
        verify(mockPlayerService!!).createNewPlayer(requestEntity.name, requestEntity.email)
    }
}