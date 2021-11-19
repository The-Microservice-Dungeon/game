//package microservice.dungeon.game.unittests.model.round.domain
//
//import microservice.dungeon.game.aggregates.round.domain.Round
//import microservice.dungeon.game.aggregates.round.domain.RoundStatus
//import microservice.dungeon.game.aggregates.round.events.RoundStarted
//import microservice.dungeon.game.aggregates.round.events.RoundStartedBuilder
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.Test
//import java.time.LocalDateTime
//import java.util.*
//
//class AbstractRoundEventTests {
//
//    @Test
//    fun makeRoundStartedFromRoundTest() {
//        val round = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)
//        val roundStarted = RoundStarted(round)
//        assertEquals(roundStarted.getRoundId(), round.getRoundId())
//        assertEquals(roundStarted.getGameId(), round.getGameId())
//        assertEquals(roundStarted.getRoundNumber(), round.getRoundNumber())
//        assertEquals(roundStarted.getRoundStatus(), RoundStatus.COMMAND_INPUT_STARTED)
//        assertTrue(roundStarted.getOccurredAt() <= LocalDateTime.now())
//    }
//
//    @Test
//    fun validateEventSerialization() {
//        val round = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)
//        val roundStarted = RoundStarted(round)
//        val roundStartedBuilder = RoundStartedBuilder()
//        val serialized: String = roundStarted.serialized()
//        val deserialized: RoundStarted = roundStartedBuilder.deserializedEvent(serialized) as RoundStarted
//        assertTrue(roundStarted.equals(deserialized))
//    }
//}