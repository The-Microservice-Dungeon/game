package microservice.dungeon.game.unittests.model.round.domain

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.Exception
import java.util.*

class CommandInputEndedTests {

    @Test
    fun checkDefaultValuesTest() {
        val round = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_ENDED)
        val commandInputEnded = CommandInputEnded(round)
        assertEquals(commandInputEnded.getTopic(), "testTopic")
        assertEquals(commandInputEnded.getEventName(), "commandInputEnded")
    }

    @Test
    fun preventCommandInputEndedCreationWithWrongRoundStatusTest() {
        val round = Round(UUID.randomUUID(), 3, UUID.randomUUID(), RoundStatus.COMMAND_INPUT_STARTED)
        Assertions.assertThrows(Exception::class.java) {
            val commandInputEnded = CommandInputEnded(round)
        }
    }
}