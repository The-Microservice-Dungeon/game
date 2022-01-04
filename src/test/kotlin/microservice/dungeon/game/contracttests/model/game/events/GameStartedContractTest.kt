package microservice.dungeon.game.contracttests.model.game.events

import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.dtos.GameEventDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class GameStartedContractTest {
    private val ANY_GAME_ID: UUID = UUID.randomUUID()

    @Test
    fun shouldSerializeToSpecificationAsDTO() {
        // given
        val gameEvent = GameEventDto(ANY_GAME_ID, GameStatus.CREATED)

        // when
        val serialized = gameEvent.serialize()

        // then
        assertThat(serialized)
            .isEqualTo(getSpecifiedOutputMessage(ANY_GAME_ID))
    }


    private fun getSpecifiedOutputMessage(gameId: UUID) =
        "{\"gameId\":\"${gameId}\",\"status\":\"created\"}"
}