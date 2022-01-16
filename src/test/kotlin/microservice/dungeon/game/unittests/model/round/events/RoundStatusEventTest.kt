package microservice.dungeon.game.unittests.model.round.events

import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStatusEvent
import microservice.dungeon.game.aggregates.round.events.RoundStatusEventBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class RoundStatusEventTest {

    @Test
    fun shouldBeLosslessSerializable() {
        // given
        val eventBuilder = RoundStatusEventBuilder("anyTopic", "anyType", 1)
        val event = eventBuilder.makeRoundStatusEvent(UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)

        // when
        val serialized = event.serialized()

        // then
        val deserialized: RoundStatusEvent = eventBuilder.deserializedEvent(serialized) as RoundStatusEvent

        assertThat(deserialized)
            .isEqualTo(event)
    }
}