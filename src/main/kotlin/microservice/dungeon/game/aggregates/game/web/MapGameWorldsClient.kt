package microservice.dungeon.game.aggregates.game.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.game.web.dto.NewGameWorldDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

class MapGameWorldsClient @Autowired constructor(
    @Value(value = "\${rest.map.baseurl}")
    private val mapBaseUrl: String
) {
    private val webClient = WebClient.create(mapBaseUrl)

    fun createNewGameWorld(numberOfPlayer: Int): Boolean {
        try {
            val requestBody = NewGameWorldDto.makeFromNumberOfPlayer(numberOfPlayer)
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
            return true
        } catch (e: Exception) {
            return false
        }
    }
}