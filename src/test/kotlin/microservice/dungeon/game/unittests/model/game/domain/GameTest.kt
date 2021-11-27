package microservice.dungeon.game.unittests.model.game.domain

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GameTest {
    private val ANY_MAXPLAYERS = 99
    private val ANY_MAXROUNDS = 99


    @Test
    fun shouldInitializeValidObject() {
        // given
        val game = Game(maxPlayers = ANY_MAXPLAYERS, maxRounds =  ANY_MAXROUNDS)

        // when
        // then
        assertThat(game)
            .isCreatedFrom(ANY_MAXPLAYERS, ANY_MAXROUNDS)
        assertThat(game.getGameId())
            .isNotNull
        assertThat(game.getGameStatus())
            .isNotNull
        assertThat(game.getMaxPlayers())
            .isEqualTo(ANY_MAXPLAYERS)
        assertThat(game.getMaxRounds())
            .isEqualTo(ANY_MAXROUNDS)
    }


}