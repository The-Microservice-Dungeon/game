package microservice.dungeon.game.web

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.web.reactive.function.client.WebClient

class CommandDispatcherClient constructor(
    private val robotBaseUrl: String
) {
    private val robotWebClient = WebClient.create(robotBaseUrl)

    fun getUserById(): JsonNode? {
        return robotWebClient
            .get()
            .uri("/users/{id}", 1)
            .retrieve()
            .bodyToMono(JsonNode::class.java)
            .block()
    }

    fun dispatchBlockingCommands(roundNumber: Int, commands: List<String>) {
        //TODO
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