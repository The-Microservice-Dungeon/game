package microservice.dungeon.game.contracttests.model.round.events

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class RoundEndedContractTest {

    private val TEST_ROUND = Round(UUID.randomUUID(), 9, UUID.randomUUID(), RoundStatus.ROUND_ENDED)

    @Test
    fun shouldSerializeToSpecificationAsDTO() {
        // given
        val roundStarted = RoundEnded(TEST_ROUND)

        // when
        val roundEventDTO = roundStarted.toDTO()

        // then
        val expectedMessage = getSpecifiedOutputMessage(roundStarted.getRoundNumber())
        assertThat(roundEventDTO.serialize())
            .isEqualTo(expectedMessage)
    }

    private fun getSpecifiedOutputMessage(roundNumber: Int) =
        "{\"roundNumber\":${roundNumber},\"roundStatus\":\"ended\"}"
}