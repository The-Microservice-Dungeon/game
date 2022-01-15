package microservice.dungeon.game.unittests.model.game.services

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStateException
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.servives.GameLoopService
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.services.RoundService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InOrder
import org.mockito.kotlin.*
import java.util.*

class GameLoopServiceTest {
    private var mockGameRepository: GameRepository? = null
    private var mockRoundService: RoundService? = null
    private var gameLoopService: GameLoopService? = null

    @BeforeEach
    fun setUp() {
        mockGameRepository = mock()
        mockRoundService = mock()
        gameLoopService = GameLoopService(
            mockGameRepository!!,
            mockRoundService!!
        )

    }

    @Test
    fun shouldAllowToStartGameLoopAndRunTwice() {
        // given (only one iteration)
        val spyGame = spy(Game(1, 2))
        spyGame.setTotalRoundTimespanInMS(1000)     // increase testing speed
        spyGame.startGame()

        whenever(mockGameRepository!!.findById(spyGame.getGameId()))
            .thenReturn(Optional.of(spyGame))

        // when
        gameLoopService!!.runGameLoop(spyGame.getGameId())

        // then
        // 1st iteration
        val inOrder: InOrder = inOrder(spyGame, mockGameRepository!!, mockRoundService!!)
        inOrder.verify(mockRoundService!!).deliverBlockingCommands(any()) // other commands as well
        inOrder.verify(mockRoundService!!).endRound(any())
        inOrder.verify(spyGame).startNewRound()
        inOrder.verify(mockGameRepository!!).save(spyGame)

        // 2nd iteration
        inOrder.verify(mockRoundService!!).deliverBlockingCommands(any())
        inOrder.verify(mockRoundService!!).endRound(any())
        inOrder.verify(spyGame).startNewRound()

        // finally
        inOrder.verify(spyGame).endGame()
        inOrder.verify(mockGameRepository!!).save(spyGame)
    }

    @Test
    fun shouldNotAllowToStartGameLoopWhenGameHasNotBeenStarted() {
        // given
        val game = Game(5,5)

        // when
        assertThrows(GameStateException::class.java) {
            gameLoopService!!.runGameLoop(game.getGameId())
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
        gameLoopService!!.executeCommandsInOrder(activeRound.getRoundId())

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
        gameLoopService!!.makeNextRound(spyGame.getGameId())

        // then
        verify(spyGame).startNewRound()
        verify(mockGameRepository!!).save(spyGame)
    }

    @Test
    fun shouldAllowToEndRound() {
        // given
        val roundId = UUID.randomUUID()

        // when
        gameLoopService!!.endRound(roundId)

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
        gameLoopService!!.endGame(game.getGameId())

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