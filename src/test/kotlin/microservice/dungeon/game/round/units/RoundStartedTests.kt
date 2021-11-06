package microservice.dungeon.game.round.units

import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import microservice.dungeon.game.aggregates.round.events.RoundEndedBuilder
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.aggregates.round.events.RoundStartedBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.time.LocalDateTime

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29092", "port=29092"])
class RoundStartedTests {

    @Test
    fun serializeAndDeserializeEventTest() {
        val event = RoundStarted(LocalDateTime.now(), 1, RoundStatus.COMMAND_INPUT_STARTED)
        val eventSerialized = event.serialized()
        val eventDeserialized = RoundStartedBuilder().deserializedEvent(eventSerialized)
        assertEquals(event.serialized(), eventDeserialized.serialized())
    }

    @Test
    fun conformEventToApiSpecificationsTest() {

    }
}