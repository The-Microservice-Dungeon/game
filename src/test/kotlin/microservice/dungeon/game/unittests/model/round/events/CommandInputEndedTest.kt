package microservice.dungeon.game.unittests.model.round.events

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.*

class CommandInputEndedTest {
    private var game = Game(10, 100)
    private var TEST_ROUND = Round(game = game, roundNumber = 3, roundStatus = RoundStatus.COMMAND_INPUT_ENDED)


    @Test
    fun newCommandInputEndedShouldInitialize() {
        val commandInputEnded = CommandInputEnded(TEST_ROUND)

        assertThat(commandInputEnded)
            .matches(TEST_ROUND)
    }

    @Test
    fun newCommandInputEndedShouldThrowWhenRoundStatusInvalid() {
        val invalidRound = Round(game = game, roundNumber = 3, roundStatus = RoundStatus.COMMAND_INPUT_STARTED)

        assertThatThrownBy {
            CommandInputEnded(invalidRound)
        }
    }
}