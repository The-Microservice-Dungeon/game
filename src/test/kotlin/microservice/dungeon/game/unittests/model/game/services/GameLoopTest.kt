package microservice.dungeon.game.unittests.model.game.services

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStateException
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.servives.GameLoop
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.services.RoundService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InOrder
import org.mockito.kotlin.*
import java.util.*

class GameLoopTest {
    private var mockGameRepository: GameRepository? = null
    private var mockRoundService: RoundService? = null
    private var gameLoop: GameLoop? = null

    @BeforeEach
    fun setUp() {
        mockGameRepository = mock()
        mockRoundService = mock()
        gameLoop = GameLoop(
            mockGameRepository!!,
            mockRoundService!!
        )

    }

    @Test
    fun shouldAllowToStartGameLoopAndRunOnce() {
        // given (only one iteration)
        val game = Game(1, 1)
        game.startGame()

        whenever(mockGameRepository!!.findById(game.getGameId()))
            .thenReturn(Optional.of(game))

        // when
        gameLoop!!.runGameLoop(game.getGameId())

        // then
        verify(mockGameRepository!!).save(argThat {
            this.getGameStatus() == GameStatus.GAME_FINISHED
        })
    }

    @Test
    fun shouldNotAllowToStartGameLoopWhenGameHasNotBeenStarted() {
        // given
        val game = Game(5,5)

        // when
        assertThrows(GameStateException::class.java) {
            gameLoop!!.runGameLoop(game.getGameId())
        }
    }

    @Test
    fun shouldPublishWhenRoundStarts() {
        assertTrue(false)
    }

    @Test
    fun shouldExecuteCommandsInOrder() {
        // given
        val game = Game(5,5)
        game.startGame()
        val activeRound: Round = game.getCurrentRound()!!

        // when
        gameLoop!!.executeCommandsInOrder(activeRound.getRoundId())

        // then
        val inOrder: InOrder = inOrder(mockRoundService!!)
        inOrder.verify(mockRoundService!!).endCommandInputs(activeRound.getRoundId())
        inOrder.verify(mockRoundService!!).deliverBlockingCommands(activeRound.getRoundId())
        inOrder.verify(mockRoundService!!).deliverTradingCommands(activeRound.getRoundId())
        inOrder.verify(mockRoundService!!).deliverMovementCommands(activeRound.getRoundId())
        inOrder.verify(mockRoundService!!).deliverBattleCommands(activeRound.getRoundId())
        inOrder.verify(mockRoundService!!).deliverMiningCommands(activeRound.getRoundId())
        inOrder.verify(mockRoundService!!).deliverRegeneratingCommands(activeRound.getRoundId())
    }

    @Test
    fun shouldAllowToStartNextRound() {
        // given
        val spyGame: Game = spy(Game(1, 2))
        spyGame.startGame()

        whenever(mockGameRepository!!.findById(spyGame.getGameId()))
                .thenReturn(Optional.of(spyGame))

        // when
        gameLoop!!.makeNextRound(spyGame.getGameId())

        // then
        verify(spyGame).startNewRound()
        verify(mockGameRepository!!).save(spyGame)
    }

    @Test
    fun shouldAllowToEndRound() {
        // given
        val roundId = UUID.randomUUID()

        // when
        gameLoop!!.endRound(roundId)

        // then
        verify(mockRoundService!!).endRound(roundId)
    }

    @Test
    fun shouldPublishWhenRoundEnds() {
        assertTrue(false)
    }

    @Test
    fun shouldAllowToEndGame() {
        // given
        val game: Game = Game(1,1)
        game.startGame()

        whenever(mockGameRepository!!.findById(game.getGameId()))
            .thenReturn(Optional.of(game))

        // when
        gameLoop!!.endGame(game.getGameId())

        // then
        verify(mockGameRepository!!).save(argThat {
            this.getGameStatus() == GameStatus.GAME_FINISHED
        })
    }

    @Test
    fun shouldPublishWhenGameEnds() {
        assertTrue(false)
    }
}

/*
        E2E Cases
            (1) Run multiple iterations
            (2) Should exit when game status set to finished
            (3) Should adapt round-time-frames when changed
 */