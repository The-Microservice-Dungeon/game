package microservice.dungeon.game.contracttests.model.round.events

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class CommandInputEndedContractTest {

    private val TEST_ROUND = Round(UUID.randomUUID(), 9, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_ENDED)

    @Test
    fun shouldSerializeToSpecificationAsDTO() {
        // given
        val roundStarted = CommandInputEnded(TEST_ROUND)

        // when
        val roundEventDTO = roundStarted.toDTO()

        // then
        val expectedMessage = getSpecifiedOutputMessage(TEST_ROUND)
        assertThat(roundEventDTO.serialize())
            .isEqualTo(expectedMessage)
    }

    private fun getSpecifiedOutputMessage(round: Round) =
        "{\"roundId\":\"${round.getRoundId()}\",\"roundNumber\":${round.getRoundNumber()},\"roundStatus\":\"command input ended\"}"
}