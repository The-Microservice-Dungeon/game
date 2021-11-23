package microservice.dungeon.game.assertions

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat

class EventDescriptorAssertion(actual: EventDescriptor):
    AbstractObjectAssert<EventDescriptorAssertion, EventDescriptor>(actual, EventDescriptorAssertion::class.java) {

    fun matches(comparison: Event): EventDescriptorAssertion {
        assertThat(actual.getId()).isEqualTo(comparison.getId())
        assertThat(actual.getType()).isEqualTo(comparison.getEventName())
        assertThat(actual.getOccurredAt()).isEqualTo(comparison.getOccurredAt().getTime())
        assertThat(actual.getContent()).isEqualTo(comparison.serialized())
        return this
    }
}