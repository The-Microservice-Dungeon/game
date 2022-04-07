package microservice.dungeon.game.integrationtests.model.player.controller

import microservice.dungeon.game.aggregates.player.controller.PlayerController
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.controller.dtos.PlayerResponseDto
import microservice.dungeon.game.aggregates.player.domain.PlayerAlreadyExistsException
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.player.services.PlayerService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.*

class PlayerControllerIntegrationTest {
    private var mockPlayerService: PlayerService? = null
    private var mockPlayerRepository: PlayerRepository? = null
    private var playerController: PlayerController? = null
    private var webTestClient: WebTestClient? = null

    @BeforeEach
    fun setUp() {
        mockPlayerService = mock()
        mockPlayerRepository = mock()
        playerController = PlayerController(mockPlayerService!!, mockPlayerRepository!!)
        webTestClient = WebTestClient.bindToController(playerController!!).build()
    }

    @Test
    fun shouldAllowToCreateNewPlayer() {
        // given
        val requestEntity = PlayerResponseDto(UUID.randomUUID(), UUID.randomUUID(), "SOME_NAME", "SOME_MAIL")
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
        assertThat(responseBody.playerId)
            .isNotNull
        verify(mockPlayerService!!).createNewPlayer(requestEntity.name, requestEntity.email)
    }

    @Test
    fun shouldRespondForbiddenWhenPlayerAlreadyExistsWhileCreatingNew() {
        // given
        val requestEntity = PlayerResponseDto(UUID.randomUUID(), UUID.randomUUID(), "SOME_NAME", "SOME_MAIL")
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

    @Test
    fun shouldAllowToFetchPlayerDetails() {
        // given
        val userName = "dadepu"
        val userMail = "dadepu@smail.th-koeln.de"
        val responsePlayer = Player(userName, userMail)
        whenever(mockPlayerRepository!!.findByUserNameAndMailAddress(userName, userMail))
            .thenReturn(Optional.of(responsePlayer))

        // when
        val result = webTestClient!!.get().uri("/players?name=${userName}&mail=${userMail}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PlayerResponseDto>()
            .returnResult()
        val responseBody = result.responseBody!!

        // then
        assertThat(responseBody.bearerToken)
            .isEqualTo(responsePlayer.getPlayerToken())
        assertThat(responseBody.name)
            .isEqualTo(responsePlayer.getUserName())
        assertThat(responseBody.email)
            .isEqualTo(responsePlayer.getMailAddress())
        assertThat(responseBody.playerId)
            .isEqualTo(responsePlayer.getPlayerId())

        // and then
        verify(mockPlayerRepository!!).findByUserNameAndMailAddress(userName, userMail)
    }

    @Test
    fun shouldRespondNotFoundWhenPlayerNotFoundWhileTryingToFetchDetails() {
        // given
        val userName = "dadepu"
        val userMail = "dadepu@smail.th-koeln.de"
        doThrow(NoSuchElementException())
            .whenever(mockPlayerRepository!!)
            .findByUserNameAndMailAddress(userName, userMail)

        // when
        webTestClient!!.get().uri("/players?name=${userName}&mail=${userMail}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound

        // then
        verify(mockPlayerRepository!!).findByUserNameAndMailAddress(userName, userMail)
    }
}
