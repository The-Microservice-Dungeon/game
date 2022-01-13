package microservice.dungeon.game.contracttests.model.round.events

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CommandInputEndedContractTest {

    private val createdGame = Game(10, 100)
    private val round = Round(game = createdGame, roundNumber = 9, roundStatus = RoundStatus.COMMAND_INPUT_ENDED)

    @Test
    fun shouldSerializeToSpecificationAsDTO() {
        // given
        val roundStarted = CommandInputEnded(round)

        // when
        val roundEventDTO = roundStarted.toDTO()

        // then
        val expectedMessage = getSpecifiedOutputMessage(round)
        assertThat(roundEventDTO.serialize())
            .isEqualTo(expectedMessage)
    }

    private fun getSpecifiedOutputMessage(round: Round) =
        "{\"roundId\":\"${round.getRoundId()}\",\"roundNumber\":${round.getRoundNumber()},\"roundStatus\":\"command input ended\"}"
}