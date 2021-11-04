package microservice.dungeon.game.aggregates.eventpublisher

import microservice.dungeon.game.aggregates.core.EventPublishing
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.messaging.KafkaProducer
import org.springframework.beans.factory.annotation.Autowired

class EventPublisherService @Autowired constructor(
    private val kafkaProducer: KafkaProducer,
    private val eventStoreService: EventStoreService
) {
    fun publishEvents(events: List<EventPublishing>) {
        events.forEach { event: EventPublishing ->
            kafkaProducer.send(event.getTopic(), event.serializeEvent())
        }
        eventStoreService.markAsPublished(events.map{ event -> event.getId()})
    }
}