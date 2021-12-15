package microservice.dungeon.game.unittests.model.round.domain

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RoundStartedTest {
    private var TEST_ROUND = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)


    @Test
    fun newRoundStartedShouldInitialize() {
        val roundStarted = RoundStarted(TEST_ROUND)

        assertThat(roundStarted)
            .matches(TEST_ROUND)
    }

    @Test
    fun newRoundStartedShouldThrowWhenInvalidRoundStatus() {
        val invalidRound = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_ENDED)

        assertThatThrownBy {
            RoundStarted(invalidRound)
        }
    }
}