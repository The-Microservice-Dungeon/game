package microservice.dungeon.game.unittests.model.game.events

import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.events.GameStatusEvent
import microservice.dungeon.game.aggregates.game.events.GameStatusEventBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class GameStatusEventTest {

    @Test
    fun shouldBeLosslessSerializable() {
        // given
        val eventBuilder = GameStatusEventBuilder("anyTopic", "anyType", 1)
        val event = eventBuilder.makeGameStatusEvent(UUID.randomUUID(), UUID.randomUUID(), GameStatus.GAME_RUNNING)

        // when
        val serialized = event.serialized()

        // then
        val deserialized: GameStatusEvent = eventBuilder.deserializedEvent(serialized) as GameStatusEvent

        assertThat(deserialized)
            .isEqualTo(event)
    }
}