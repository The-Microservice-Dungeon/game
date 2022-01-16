package microservice.dungeon.game.unittests.model.game.events

import microservice.dungeon.game.aggregates.game.events.PlayerStatusEvent
import microservice.dungeon.game.aggregates.game.events.PlayerStatusEventBuilder
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class PlayerStatusEventTest {

    @Test
    fun shouldBeLosslessSerializable() {
        // given
        val eventBuilder = PlayerStatusEventBuilder("anyTopic", "anyType", 1)
        val event = eventBuilder.makePlayerStatusEvent(UUID.randomUUID(), UUID.randomUUID(), "dadepu")

        // when
        val serialized = event.serialized()

        // then
        val deserialized: PlayerStatusEvent = eventBuilder.deserializedEvent(serialized) as PlayerStatusEvent

        Assertions.assertThat(deserialized)
            .isEqualTo(event)
    }
}