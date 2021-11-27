package microservice.dungeon.game.assertions

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.events.PlayerCreated
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat

class GameAssertion(actual: Game):
    AbstractObjectAssert<GameAssertion, Game>(actual, GameAssertion::class.java){

    fun isCreatedFrom(maxPlayers: Int, maxRounds: Int): GameAssertion {
        assertThat(actual.getMaxRounds())
            .isEqualTo(maxRounds)
        assertThat(actual.getMaxPlayers())
            .isEqualTo(maxPlayers)
        return this
    }

    fun isSameAs(other: Game): GameAssertion {
        assertThat(actual.getGameId())
            .isEqualTo(other.getGameId())
        assertThat(actual.getGameStatus())
            .isEqualTo(other.getGameStatus())
        assertThat(actual.getMaxRounds())
            .isEqualTo(other.getMaxRounds())
        assertThat(actual.getMaxPlayers())
            .isEqualTo(other.getMaxPlayers())
        return this
    }
}