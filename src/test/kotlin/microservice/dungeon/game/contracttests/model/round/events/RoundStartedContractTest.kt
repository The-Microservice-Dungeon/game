package microservice.dungeon.game.contracttests.model.round.events

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class RoundStartedContractTest {

    private val TEST_ROUND = Round(UUID.randomUUID(), 9, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)

    @Test
    fun shouldSerializeToSpecificationAsDTO() {
        // given
        val roundStarted = RoundStarted(TEST_ROUND)

        // when
        val roundEventDTO = roundStarted.toDTO()

        // then
        val expectedMessage = getSpecifiedOutputMessage(roundStarted.getRoundNumber())
        assertThat(roundEventDTO.serialize())
            .isEqualTo(expectedMessage)
    }

    private fun getSpecifiedOutputMessage(roundNumber: Int) =
        "{\"roundNumber\":${roundNumber},\"roundStatus\":\"started\"}"
}