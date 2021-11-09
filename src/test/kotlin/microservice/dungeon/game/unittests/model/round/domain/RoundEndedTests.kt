package microservice.dungeon.game.unittests.model.round.domain

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.Exception
import java.util.*


class RoundEndedTests {

    @Test
    fun checkDefaultValuesTest() {
        val round = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.ROUND_ENDED)
        val roundEnded = RoundEnded(round)
        assertEquals(roundEnded.getTopic(), "testTopic")
        assertEquals(roundEnded.getEventName(), "roundEnded")
    }

    @Test
    fun preventRoundEndedCreationWithWrongRoundStatusTest() {
        val round = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)
        Assertions.assertThrows(Exception::class.java) {
            val roundEnded = RoundEnded(round)
        }
    }
}