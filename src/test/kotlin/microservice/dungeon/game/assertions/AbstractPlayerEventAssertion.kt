package microservice.dungeon.game.assertions

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.events.AbstractPlayerEvent
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat

class AbstractPlayerEventAssertion(actual: AbstractPlayerEvent):
    AbstractObjectAssert<AbstractPlayerEventAssertion, AbstractPlayerEvent>(actual, AbstractPlayerEventAssertion::class.java) {

    fun matches(player: Player): AbstractPlayerEventAssertion {
        assertThat(actual.getTransactionId())
            .isEqualTo(player.getPlayerId())
        assertThat(actual.getPlayerId())
            .isEqualTo(player.getPlayerId())
        assertThat(actual.getUserName())
            .isEqualTo(player.getUserName())
        assertThat(actual.getMailAddress())
            .isEqualTo(player.getMailAddress())
        return this
    }
}