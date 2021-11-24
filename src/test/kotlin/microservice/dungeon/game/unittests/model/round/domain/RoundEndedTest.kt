package microservice.dungeon.game.unittests.model.round.domain

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.assertions.CustomAssertions
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.Exception
import java.util.*

class RoundEndedTest {
    private var validRound: Round? = null

    @BeforeEach
    fun setUp() {
        validRound = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.ROUND_ENDED)
    }


    @Test
    fun newRoundEndedShouldInitialize() {
        val roundEnded = RoundEnded(validRound!!)

        assertThat(roundEnded)
            .matches(validRound!!)
    }

    @Test
    fun newRoundEndedShouldThrowWhenInvalidRoundStatus() {
        val invalidRound = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)

        assertThatThrownBy {
            RoundEnded(invalidRound)
        }
    }
}