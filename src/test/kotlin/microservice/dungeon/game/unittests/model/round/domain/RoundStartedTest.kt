package microservice.dungeon.game.unittests.model.round.domain

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.data.repository.query.Param
import java.lang.Exception
import java.util.*
import java.util.stream.Stream

class RoundStartedTest {
    private var validRound: Round? = null

    @BeforeEach
    fun setUp() {
        validRound = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)
    }


    @Test
    fun newRoundStartedShouldInitialize() {
        val roundStarted = RoundStarted(validRound!!)

        assertThat(roundStarted)
            .matches(validRound!!)
    }

    @Test
    fun newRoundStartedShouldThrowWhenInvalidRoundStatus() {
        val invalidRound = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_ENDED)

        assertThatThrownBy {
            RoundStarted(invalidRound)
        }
    }
}