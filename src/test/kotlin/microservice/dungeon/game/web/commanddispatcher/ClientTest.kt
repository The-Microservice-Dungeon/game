package microservice.dungeon.game.web.commanddispatcher

import com.fasterxml.jackson.databind.JsonNode
import microservice.dungeon.game.web.CommandDispatcherClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.springframework.test.annotation.DirtiesContext
import java.util.concurrent.TimeUnit


@DirtiesContext
class ClientTest {

    @Test
    fun testRestLoop() {
        val mockWebServer = MockWebServer()
        mockWebServer.start()

        val mockResponse: MockResponse = MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody("{\"id\": 1, \"name\":\"duke\"}")
            .throttleBody(16, 5, TimeUnit.SECONDS);

        mockWebServer.enqueue(mockResponse);

        val client = CommandDispatcherClient(mockWebServer.url("/").toString())
        val response: JsonNode? = client.getUserById()

        val requests = mockWebServer.takeRequest()
        println(requests)
        println("fetched Response: $response")
    }
}