package microservice.dungeon.game.unittests.model.game.web.dto

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.game.web.dto.NewGameWorldDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NewGameWorldDtoTest {

    @Test
    fun shouldSerializeToSpecifications() {
        // given
        val dto = NewGameWorldDto.makeFromNumberOfPlayer(3)
        val expected = "{\"gameworld\":{\"player_amount\":3}}"

        // when
        val serialized = ObjectMapper().writeValueAsString(dto)

        // then
        assertThat(serialized)
            .isEqualTo(expected)
    }
}