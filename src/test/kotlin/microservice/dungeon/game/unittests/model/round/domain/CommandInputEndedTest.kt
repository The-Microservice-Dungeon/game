package microservice.dungeon.game.unittests.model.round.domain

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.*

class CommandInputEndedTest {
    private var TEST_ROUND = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_ENDED)


    @Test
    fun newCommandInputEndedShouldInitialize() {
        val commandInputEnded = CommandInputEnded(TEST_ROUND)

        assertThat(commandInputEnded)
            .matches(TEST_ROUND)
    }

    @Test
    fun newCommandInputEndedShouldThrowWhenRoundStatusInvalid() {
        val invalidRound = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)

        assertThatThrownBy {
            CommandInputEnded(invalidRound)
        }
    }
}