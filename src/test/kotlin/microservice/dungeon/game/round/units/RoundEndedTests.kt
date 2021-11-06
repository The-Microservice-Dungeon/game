package microservice.dungeon.game.round.units

import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import microservice.dungeon.game.aggregates.round.events.CommandInputEndedBuilder
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import microservice.dungeon.game.aggregates.round.events.RoundEndedBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.time.LocalDateTime

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29092", "port=29092"])
class RoundEndedTests {

    @Test
    fun serializeAndDeserializeEventTest() {
        val event = RoundEnded(LocalDateTime.now(), 1, RoundStatus.COMMAND_INPUT_STARTED)
        val eventSerialized = event.serialized()
        val eventDeserialized = RoundEndedBuilder().deserializedEvent(eventSerialized)
        Assertions.assertEquals(event.serialized(), eventDeserialized.serialized())
    }

    @Test
    fun conformEventToApiSpecificationsTest() {

    }
}