package microservice.dungeon.game.unittests.model.game.domain

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GameTest {


    @Test
    fun shouldAllowToStartTheGame() {
        // given
        val game: Game = Game(10, 60)

        // when
        game.startGame()

        // then
        assertThat(game.getGameStatus())
            .isEqualTo(GameStatus.GAME_RUNNING)
        assertThat(game.getCurrentRound()!!.getRoundNumber())
            .isEqualTo(1)

        // and
        val currentRound: Round = game.getCurrentRound()!!
        assertThat(currentRound.getGame())
            .isEqualTo(game)
        assertThat(currentRound.getRoundNumber())
            .isEqualTo(1)
        assertThat(currentRound.getRoundStatus())
            .isEqualTo(RoundStatus.COMMAND_INPUT_STARTED)
    }

    @Test
    fun shouldOnlyBeStartAbleWhenStatusIsCreated() {
        // given
        val game: Game = Game(10, 100)
        game.startGame()

        // when
        assertThatThrownBy {
            game.startGame()
        }
    }

    // same for round

    @Test
    fun shouldAllowToEndTheGame() {
        // given
        val game: Game = Game(10, 100)
        game.startGame()

        // when
        game.endGame()

        // then
        assertThat(game.getGameStatus())
            .isEqualTo(GameStatus.GAME_FINISHED)
    }
}