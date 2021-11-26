package microservice.dungeon.game.assertions

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.player.dtos.PlayerEventDto
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class PlayerEventDtoAssertion(actual: PlayerEventDto):
    AbstractObjectAssert<PlayerEventDtoAssertion, PlayerEventDto>(actual, PlayerEventDtoAssertion::class.java) {

    private val objectMapper = ObjectMapper().findAndRegisterModules()

    fun containsPlayerId(otherPlayerId: UUID): PlayerEventDtoAssertion {
        // when
        val node: JsonNode = objectMapper.readTree(actual.serialize())
        val playerId: String = node.path("playerId").asText()
        // then
        assertThat(UUID.fromString(playerId))
            .isEqualTo(otherPlayerId)
        return this
    }

    fun containsUserName(otherUserName: String): PlayerEventDtoAssertion {
        // when
        val node: JsonNode = objectMapper.readTree(actual.serialize())
        val userName: String = node.path("userName").asText()
        // then
        assertThat(userName)
            .isEqualTo(otherUserName)
        return this
    }

    fun containsMailAddress(otherMailAddress: String): PlayerEventDtoAssertion {
        // when
        val node: JsonNode = objectMapper.readTree(actual.serialize())
        val mailAddress: String = node.path("mailAddress").asText()
        // then
        assertThat(mailAddress)
            .isEqualTo(otherMailAddress)
        return this
    }
}