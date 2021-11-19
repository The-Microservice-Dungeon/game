//package microservice.dungeon.game.unittests.model.round.domain
//
//import microservice.dungeon.game.aggregates.round.domain.Round
//import microservice.dungeon.game.aggregates.round.domain.RoundStatus
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//import java.util.*
//
//class RoundTests {
//
//    @Test
//    fun makeNewGameTest() {
//        val roundId = UUID.randomUUID()
//        val gameId = UUID.randomUUID()
//        val roundNumber = 3
//        val round = Round(gameId, roundNumber, roundId)
//        assertEquals(round.getRoundId(), roundId)
//        assertEquals(round.getGameId(), gameId)
//        assertEquals(round.getRoundNumber(), roundNumber)
//        assertEquals(round.getRoundStatus(), RoundStatus.COMMAND_INPUT_STARTED)
//    }
//
//    @Test
//    fun roundStatusSequenceWithoutScoutingIterationTest() {
//        val round = Round(UUID.randomUUID(), 2)
//        assertEquals(round.getRoundStatus(), RoundStatus.COMMAND_INPUT_STARTED)
//        round.endCommandInputPhase()
//        assertEquals(round.getRoundStatus(), RoundStatus.COMMAND_INPUT_ENDED)
//        round.deliverBlockingCommandsToRobot()
//        assertEquals(round.getRoundStatus(), RoundStatus.BLOCKING_COMMANDS_DISPATCHED)
//        round.deliverTradingCommandsToRobot()
//        assertEquals(round.getRoundStatus(), RoundStatus.TRADING_COMMANDS_DISPATCHED)
//        round.deliverMovementCommandsToRobot()
//        assertEquals(round.getRoundStatus(), RoundStatus.MOVEMENT_COMMANDS_DISPATCHED)
//        round.deliverBattleCommandsToRobot()
//        assertEquals(round.getRoundStatus(), RoundStatus.BATTLE_COMMANDS_DISPATCHED)
//        round.deliverMiningCommandsToRobot()
//        assertEquals(round.getRoundStatus(), RoundStatus.MINING_COMMANDS_DISPATCHED)
//        round.endRound()
//        assertEquals(round.getRoundStatus(), RoundStatus.ROUND_ENDED)
//    }
//}