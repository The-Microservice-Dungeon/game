package microservice.dungeon.game.aggregates.game.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.game.web.dto.NewGameWorldDto
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Scope
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
@Scope("singleton")
class MapGameWorldsClient @Autowired constructor(
    @Value(value = "\${rest.map.baseurl}")
    private val mapBaseUrl: String
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val webClient = WebClient.create(mapBaseUrl)

    fun createNewGameWorld(numberOfPlayer: Int): Boolean {
        logger.debug("Starting to request MapService to create a new game-world ... [playerCount=$numberOfPlayer]")

        try {
            val requestBody = NewGameWorldDto.makeFromNumberOfPlayer(numberOfPlayer)
            logger.trace("Endpoint is: POST ${mapBaseUrl}/gameworlds")
            logger.trace(ObjectMapper().writeValueAsString(requestBody))

            webClient.post().uri("/gameworlds")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(ObjectMapper().writeValueAsString(requestBody))
                .exchangeToMono { clientResponse ->
                    if (clientResponse.statusCode() == HttpStatus.CREATED) {
                        clientResponse.bodyToMono(JsonNode::class.java)
                    } else {
                        throw Exception("Connection failed w/ status-code: ${clientResponse.statusCode()}")
                    }
                }.block()

            logger.debug("Request to MapService successful.")
            return true
        } catch (e: Exception) {

            logger.error("Request to MapService failed.")
            logger.error(e.message)
            return false
        }
    }
}