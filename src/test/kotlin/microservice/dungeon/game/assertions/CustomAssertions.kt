package microservice.dungeon.game.assertions

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import org.assertj.core.api.Assertions

class CustomAssertions: Assertions() {
    companion object {
        fun assertThat(actual: Event) = EventAssertion(actual)
        fun assertThat(actual: EventDescriptor) = EventDescriptorAssertion(actual)
    }
}