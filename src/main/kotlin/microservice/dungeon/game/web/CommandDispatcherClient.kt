package microservice.dungeon.game.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

class CommandDispatcherClient constructor(
    private val robotBaseUrl: String
) {
    private val robotWebClient = WebClient.create(robotBaseUrl)

    fun dispatchBlockingCommands(roundNumber: Int, commands: List<String>): JsonNode? {
        return robotWebClient
            .post()
            .uri("/commands/blocking")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(ObjectMapper().writeValueAsString(commands))
            .exchangeToMono{ clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.OK) {
                    clientResponse.bodyToMono(JsonNode::class.java)
                }
                else {
                    throw Exception("Err")
                }
            }.block()
    }

    fun dispatchTradingCommands(roundNumber: Int, commands: List<String>) {
        //TODO
    }

    fun dispatchMovementCommands(roundNumber: Int, commands: List<String>) {
        //TODO
    }

    fun dispatchBattleCommands(roundNumber: Int, commands: List<String>) {
        //TODO
    }

    fun dispatchMiningCommands(roundNumber: Int, commands: List<String>) {
        //TODO
    }

    fun dispatchScoutingCommands(roundNumber: Int, commands: List<String>) {
        //TODO
    }
}