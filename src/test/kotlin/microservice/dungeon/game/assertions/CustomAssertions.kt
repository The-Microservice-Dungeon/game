package microservice.dungeon.game.assertions

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import microservice.dungeon.game.aggregates.round.events.AbstractRoundEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.assertj.core.api.Assertions

class CustomAssertions: Assertions() {
    companion object {
        fun assertThat(actual: AbstractRoundEvent) = AbstractRoundEventAssertion(actual)
        fun assertThat(actual: Event) = EventAssertion(actual)
        fun assertThat(actual: EventDescriptor) = EventDescriptorAssertion(actual)
        fun assertThat(actual: ProducerRecord<String, String>) = ProducerRecordAssertion(actual)
    }
}