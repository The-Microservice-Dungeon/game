package microservice.dungeon.game.model.round.domaintests

import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.aggregates.round.events.RoundStartedBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.time.LocalDateTime
import java.util.*

class RoundStartedTests {

    @Test
    fun serializeAndDeserializeEventTest() {
        val event = RoundStarted(LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID(), 1, RoundStatus.COMMAND_INPUT_STARTED)
        val eventSerialized = event.serialized()
        val eventDeserialized = RoundStartedBuilder().deserializedEvent(eventSerialized)
        assertEquals(event.serialized(), eventDeserialized.serialized())
    }

    @Test
    fun conformEventToApiSpecificationsTest() {

    }
}