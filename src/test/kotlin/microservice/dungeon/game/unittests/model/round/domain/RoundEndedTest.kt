package microservice.dungeon.game.unittests.model.round.domain

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RoundEndedTest {
    private var game = Game(10, 100)
    private var TEST_ROUND = Round(game = game, roundNumber = 3, roundStatus = RoundStatus.ROUND_ENDED)


    @Test
    fun newRoundEndedShouldInitialize() {
        val roundEnded = RoundEnded(TEST_ROUND)

        assertThat(roundEnded)
            .matches(TEST_ROUND)
    }

    @Test
    fun newRoundEndedShouldThrowWhenInvalidRoundStatus() {
        val invalidRound = Round(game = game, roundNumber = 3, roundStatus =  RoundStatus.COMMAND_INPUT_STARTED)

        assertThatThrownBy {
            RoundEnded(invalidRound)
        }
    }
}