package microservice.dungeon.game.aggregates.eventpublisher

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.messaging.producer.KafkaProducing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventPublisherService @Autowired constructor(
    private val kafkaProducing: KafkaProducing,
    private val eventStoreService: EventStoreService
) {
    fun publishEvents(events: List<Event>) {
        events.forEach { event: Event ->
            kafkaProducing.send(event.getTopic(), event.toDTO().serialize())
        }
    }

    fun onSuccessfulPublish(eventId: UUID) {
        eventStoreService.markAsPublished(listOf(eventId))
    }
}