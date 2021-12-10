package microservice.dungeon.game.aggregates.round.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.command.dtos.BuyCommandDTO
import microservice.dungeon.game.aggregates.command.dtos.SellCommandDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

class TradingCommandDispatcherClient @Autowired constructor(
    @Value(value = "\${rest.trading.baseurl}")
    private val tradingBaseURL: String
) {
    private val webClient = WebClient.create(tradingBaseURL)



    fun sendSellingCommands(commands: List<SellCommandDTO>) {
        transmitCommandsToTrading(commands)
    }

    fun sendBuyingCommands(commands: List<BuyCommandDTO>) {
        transmitCommandsToTrading(commands)
    }

    private fun transmitCommandsToTrading(commands: List<Any>) {
        webClient.post().uri("/commands")
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
}