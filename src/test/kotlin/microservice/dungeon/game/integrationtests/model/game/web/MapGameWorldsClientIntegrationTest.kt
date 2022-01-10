package microservice.dungeon.game.integrationtests.model.game.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import microservice.dungeon.game.aggregates.game.web.MapGameWorldsClient
import microservice.dungeon.game.aggregates.game.web.dto.NewGameWorldDto
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

class MapGameWorldsClientIntegrationTest {
    private var mockWebServer: MockWebServer? = null
    private var mapGameWorldsClient: MapGameWorldsClient? = null

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())


    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer!!.start()
        mapGameWorldsClient = MapGameWorldsClient(mockWebServer!!.url("/").toString())
    }

    @Test
    fun shouldAllowToCreateANewGameWorld() {
        // given
        val numberOfPlayer = 5
        val mockResponse = MockResponse()
            .setResponseCode(201)
        mockWebServer!!.enqueue(mockResponse)

        // when
        val response = mapGameWorldsClient!!.createNewGameWorld(numberOfPlayer)

        // and
        val capturedRequest = mockWebServer!!.takeRequest()
        val capturedNewGameWorldDto: NewGameWorldDto = objectMapper.readValue(
            capturedRequest.body.readUtf8()
        )

        // then
        assertThat(response)
            .isTrue

        // and
        assertThat(capturedRequest.method)
            .isEqualTo("POST")
        assertThat(capturedRequest.path)
            .isEqualTo("/gameworlds")
        assertThat(capturedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        // and
        assertThat(capturedNewGameWorldDto)
            .isEqualTo(NewGameWorldDto.makeFromNumberOfPlayer(numberOfPlayer))
    }

    @Test
    fun shouldReturnFalseWhenGameWorldCreationFailed() {
        // given
        val anyPlayerNumber = 3
        val mockResponse = MockResponse()
            .setResponseCode(500)
        mockWebServer!!.enqueue(mockResponse)

        // when
        val response = mapGameWorldsClient!!.createNewGameWorld(anyPlayerNumber)

        // then
        assertThat(response)
            .isFalse
    }
}