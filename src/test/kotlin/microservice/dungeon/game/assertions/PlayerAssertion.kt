package microservice.dungeon.game.assertions

import microservice.dungeon.game.aggregates.player.domain.Player
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat

class PlayerAssertion(actual: Player):
    AbstractObjectAssert<PlayerAssertion, Player>(actual, PlayerAssertion::class.java){

    fun isCreatedFrom(userName: String, mailAddress: String): PlayerAssertion {
        assertThat(actual.getUserName())
            .isEqualTo(userName)
        assertThat(actual.getMailAddress())
            .isEqualTo(mailAddress)
        return this
    }

    fun isSameAs(other: Player): PlayerAssertion {
        assertThat(actual.getPlayerId())
            .isEqualTo(other.getPlayerId())
        assertThat(actual.getPlayerToken())
            .isEqualTo(other.getPlayerToken())
        assertThat(actual.getUserName())
            .isEqualTo(other.getUserName())
        assertThat(actual.getMailAddress())
            .isEqualTo(other.getMailAddress())
        return this
    }
}