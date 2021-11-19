//package microservice.dungeon.game.unittests.model.round.domain
//
//import microservice.dungeon.game.aggregates.round.domain.Round
//import microservice.dungeon.game.aggregates.round.domain.RoundStatus
//import microservice.dungeon.game.aggregates.round.events.RoundStarted
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import java.lang.Exception
//import java.util.*
//
//class RoundStartedTests {
//
//    @Test
//    fun checkDefaultValuesTest() {
//        val round = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)
//        val roundStarted = RoundStarted(round)
//        assertEquals(roundStarted.getTopic(), "testTopic")
//        assertEquals(roundStarted.getEventName(), "roundStarted")
//    }
//
//    @Test
//    fun preventRoundStartedCreationWithWrongRoundStatusTest() {
//        val round = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_ENDED)
//        assertThrows(Exception::class.java) {
//            val roundStarted = RoundStarted(round)
//        }
//    }
//}