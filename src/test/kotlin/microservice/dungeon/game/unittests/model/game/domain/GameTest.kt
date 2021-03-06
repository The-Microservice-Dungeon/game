package microservice.dungeon.game.unittests.model.game.domain

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameParticipationException
import microservice.dungeon.game.aggregates.game.domain.GameStateException
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class GameTest {

    private var player: Player? = null

    @BeforeEach
    fun setUp() {
        player = Player("dadepu", "dadepu@smail.th-koeln.de")
    }

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
        assertThrows(GameStateException::class.java) {
            game.startGame()
        }
    }

    @Test
    fun shouldAllowToStartANewRound() {
        // given
        val game: Game = Game(10, 100)
        game.startGame()

        // when
        game.startNewRound()

        // then
        val currentRound: Round = game.getCurrentRound()!!
        assertThat(currentRound.getRoundNumber())
            .isEqualTo(2)
        assertThat(currentRound.getRoundStatus())
            .isEqualTo(RoundStatus.COMMAND_INPUT_STARTED)

        // and
        val previousRound: Round = game.getRound(1)
        assertThat(previousRound.getRoundStatus())
            .isEqualTo(RoundStatus.ROUND_ENDED)
    }

    @Test
    fun shouldNotBeAbleToStartNewRoundWhenStatusIsOtherThanRunning() {
        // given
        val game: Game = Game(10, 100)

        // when
        assertThrows(GameStateException::class.java) {
            game.startNewRound()
        }
    }

    @Test
    fun shouldNotBeAbleToStartNewRoundWhenMaximumNumberOfRoundsIsReached() {
        // given
        val game: Game = Game(1,1)
        game.startGame()

        // when
        assertThrows(GameStateException::class.java) {
            game.startNewRound()
        }
    }

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

    @Test
    fun shouldAllowPlayersToJoinTheGame() {
        // given
        val game: Game = Game(10, 100)

        // when
        game.joinGame(player!!)

        // then
        assertThat(game.getParticipatingPlayers())
            .contains(player!!)
    }

    @Test
    fun shouldNotAllowPlayersToJoinARunningOrFinishedGame() {
        // given
        val game: Game = Game(10, 100)
        game.startGame()

        // when
        assertThrows(GameStateException::class.java) {
            game.joinGame(player!!)
        }
    }

    @Test
    fun shouldNotAllowSamePlayerJoinMoreThanOnce() {
        // given
        val game: Game = Game(10, 100)
        val playerClone: Player = Player(player!!.getPlayerId(), player!!.getPlayerToken(), player!!.getUserName(), player!!.getMailAddress())
        game.joinGame(player!!)

        // when
        assertThrows(GameParticipationException::class.java) {
            game.joinGame(playerClone)
        }
    }

    @Test
    fun shouldNotAllowMorePlayersToJoinThanMaximumNumberOfPlayersPermits() {
        // given
        val numberOfMaxPlayer = 1
        val game: Game = Game(numberOfMaxPlayer, 100)
        val otherPlayer: Player = Player("mel", "some mail")
        game.joinGame(player!!)

        // when
        assertThrows(GameParticipationException::class.java) {
            game.joinGame(otherPlayer)
        }
    }

    @Test
    fun shouldAllowToRetrieveTimeWhenGameStarted() {
        // given
        val game = Game(10, 10)

        // when
        val timeStartedBeforeStarting: LocalDateTime? = game.getTimeGameStartedTruncatedToSeconds()

        // then
        assertThat(timeStartedBeforeStarting)
            .isNull()

        // when
        game.startGame()
        val timeStartedAfterStarting: LocalDateTime? = game.getTimeGameStartedTruncatedToSeconds()

        // then
        assertThat(timeStartedAfterStarting)
            .isBeforeOrEqualTo(LocalDateTime.now())

        // when
        game.startNewRound()
        val timeStartedAfterNextRound: LocalDateTime? = game.getTimeGameStartedTruncatedToSeconds()

        // then
        assertThat(timeStartedAfterNextRound)
            .isEqualTo(timeStartedAfterStarting)
        assertThat(game.getCurrentRound()!!.getRoundNumber())
            .isEqualTo(2)
        assertThat(timeStartedAfterNextRound)
            .isEqualTo(game.getRound(1).getRoundStarted())
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenNumberIsLowerThanOneWhenTryingToChangeNumberOfMaximumRounds() {
        // given
        val numberLowerThanOne = 0
        val game = Game(1,5)

        // when
        assertThrows(IllegalArgumentException::class.java) {
            game.changeMaximumNumberOfRounds(numberLowerThanOne)
        }
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenNumberIsBelowCurrentRoundCountWhenTryingToChangeNumberOfMaximumRounds() {
        // given
        val numberBelowCurrentCount = 2
        val game = Game(1,5)
        game.startGame()
        game.startNewRound()
        game.startNewRound()

        // when
        assertThrows(IllegalArgumentException::class.java) {
            game.changeMaximumNumberOfRounds(numberBelowCurrentCount)
        }
    }

    @Test
    fun shouldThrowGameStateExceptionWhenGameStateIsFinishedWhenTryingToChangeRoundDuration() {
        // given
        val anyValidNumber: Long = 60000
        val finishedGame = Game(1,1)
        finishedGame.endGame()

        // when
        assertThrows(GameStateException::class.java) {
            finishedGame.changeRoundDuration(anyValidNumber)
        }
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenDurationIsBelowTwoSecondsWhenTryingToChangeRoundDuration() {
        // given
        val belowOneSecond: Long = 1999
        val game = Game(1,2)
        game.startGame()

        // when
        assertThrows(IllegalArgumentException::class.java) {
            game.changeRoundDuration(belowOneSecond)
        }
    }
}