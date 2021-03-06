package microservice.dungeon.game.aggregates.round.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.round.web.dto.BuyCommandDto
import microservice.dungeon.game.aggregates.round.web.dto.SellCommandDto
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class TradingCommandDispatcherClient @Autowired constructor(
    @Value(value = "\${rest.trading.baseurl}")
    private val tradingBaseURL: String
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val webClient = WebClient.create(tradingBaseURL)

    fun sendSellingCommands(commands: List<SellCommandDto>) {
        if (commands.isEmpty()) {
            logger.debug("No Selling-Commands to dispatch.")
            return
        }
        return try {
            logger.debug("Starting to dispatch {} Selling-Commands to TradingService ...", commands.size)
            transmitCommandsToTrading(commands)
            logger.debug("... dispatching of Selling-Commands completed.")

        } catch (e: Exception) {
            logger.error("... dispatching of Selling-Commands failed!")
            logger.error(e.message)
        }
    }

    fun sendBuyingCommands(commands: List<BuyCommandDto>) {
        if (commands.isEmpty()) {
            logger.debug("No Buying-Commands to dispatch.")
            return
        }
        return try {
            logger.debug("Starting to dispatch {} Buying-Commands to TradingService ...", commands.size)
            transmitCommandsToTrading(commands)
            logger.debug("... dispatching of Buying-Commands completed.")

        } catch (e: Exception) {
            logger.error("... dispatching of Buying-Commands failed!")
            logger.error(e.message)
        }
    }

    private fun transmitCommandsToTrading(commands: List<Any>) {
        logger.trace("Trading-Endpoint is: POST ${tradingBaseURL}/commands")
        logger.trace(ObjectMapper().writeValueAsString(commands))

        webClient.post().uri("/commands")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(commands)
            .exchangeToMono{ clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.OK) {
                    clientResponse.bodyToMono(JsonNode::class.java)
                }
                else {
                    throw Exception("Connection failed w/ status-code: ${clientResponse.statusCode()}")
                }
            }.block()
    }
}