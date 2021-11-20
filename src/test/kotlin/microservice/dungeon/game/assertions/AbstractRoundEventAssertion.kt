package microservice.dungeon.game.assertions

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.events.AbstractRoundEvent
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat

class AbstractRoundEventAssertion(actual: AbstractRoundEvent):
    AbstractObjectAssert<AbstractRoundEventAssertion, AbstractRoundEvent>(actual, AbstractRoundEventAssertion::class.java){

    fun matches(round: Round): AbstractRoundEventAssertion {
        assertThat(actual.getTransactionId())
            .isEqualTo(round.getRoundId())
        assertThat(actual.getRoundId())
            .isEqualTo(round.getRoundId())
        assertThat(actual.getGameId())
            .isEqualTo(round.getGameId())
        assertThat(actual.getRoundNumber())
            .isEqualTo(round.getRoundNumber())
        assertThat(actual.getRoundStatus())
            .isEqualTo(round.getRoundStatus())
        return this
    }
}