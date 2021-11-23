package microservice.dungeon.game.assertions

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat

class EventAssertion(actual: Event):
    AbstractObjectAssert<EventAssertion, Event>(actual, EventAssertion::class.java) {

    fun isSameAs(comparison: Event): EventAssertion {
        assertThat(actual.isSameAs(comparison)).isTrue
        return this
    }

    fun matches(comparison: EventDescriptor): EventAssertion {
        assertThat(comparison).matches(actual)
        return this
    }
}