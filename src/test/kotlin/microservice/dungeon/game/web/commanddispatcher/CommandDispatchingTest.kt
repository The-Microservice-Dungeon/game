package microservice.dungeon.game.web.commanddispatcher

import com.fasterxml.jackson.databind.JsonNode
import microservice.dungeon.game.web.CommandDispatcherClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import org.springframework.test.annotation.DirtiesContext
import java.util.concurrent.TimeUnit

@DirtiesContext
class CommandDispatchingTest {

    @Test
    fun postCommandSuccessfulTest() {
        val mockWebServer = MockWebServer()
        mockWebServer.start()
        val mockResponse: MockResponse = MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody("{\"id\": 1, \"name\":\"duke\"}")
            .throttleBody(16, 5, TimeUnit.SECONDS);
        mockWebServer.enqueue(mockResponse);
        val client = CommandDispatcherClient(mockWebServer.url("/").toString())
        val response: JsonNode? = client.dispatchBlockingCommands(3, listOf("Daniel, Nils"))
        val requests = mockWebServer.takeRequest().body.readUtf8()
        println("fetched Requests: $requests")
        println("fetched Response: $response")
    }
}