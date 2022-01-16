package microservice.dungeon.game.unittests.model.round.events

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.AbstractRoundEvent
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.aggregates.round.events.RoundStartedBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.*

class AbstractRoundEventTest {
    private var game = Game(10, 100)
    private var validRound: Round? = null
    private var validRoundStarted: AbstractRoundEvent? = null

    @BeforeEach
    fun setUp() {
        validRound = Round(game = game, roundNumber = 3, roundStatus = RoundStatus.COMMAND_INPUT_STARTED)
        validRoundStarted = RoundStarted(validRound!!)
    }


    @Test
    fun equalsShouldBeTrueWhenBothObjectsAreEqualByValue() {
        val event = RoundStarted(validRound!!)
        val equalEvent = RoundStarted(event.getId(), event.getOccurredAt(), event.getRoundId(), event.getGameId(), event.getRoundNumber(), event.getRoundStatus())

        assertThat(event)
            .isEqualTo(equalEvent)
    }

    @Test
    fun equalsShouldBeFalseWhenBothObjectsAreNotEqualByValue() {
        val event = RoundStarted(validRound!!)
        val differentEvent = RoundStarted(UUID.randomUUID(), event.getOccurredAt(), event.getRoundId(), event.getGameId(), event.getRoundNumber(), event.getRoundStatus())

        assertThat(event)
            .isNotEqualTo(differentEvent)
    }

    @Test
    @Disabled
    fun hashCodeShouldBeEqualWhenBothObjectsAreEqualByValue() {

    }

    @Test
    fun objectShouldBeSerializableWithOutLoss() {
        val event = RoundStarted(validRound!!)

        val serializedEvent: String = event.serialized()
        val deserializedEvent = RoundStartedBuilder().deserializedEvent(serializedEvent)

        assertThat(event)
            .isEqualTo(deserializedEvent)
    }

    @Test
    fun isSameShouldBeTrueForEqualEvents() {
        val event = RoundStarted(validRound!!)
        val equalEvent = RoundStarted(event.getId(), event.getOccurredAt(), event.getRoundId(), event.getGameId(), event.getRoundNumber(), event.getRoundStatus())

        assertThat(event.isSameAs(equalEvent))
            .isTrue
    }
}