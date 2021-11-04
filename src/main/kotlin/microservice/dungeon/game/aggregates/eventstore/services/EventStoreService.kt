package microservice.dungeon.game.aggregates.eventstore.services

import microservice.dungeon.game.aggregates.core.InvalidApplicationPropertyException
import microservice.dungeon.game.aggregates.core.EventPublishing
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptorStatus
import microservice.dungeon.game.aggregates.eventstore.repositories.EventDescriptorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import java.util.*

class EventStoreService @Autowired constructor(
    private val eventdescriptorRepository: EventDescriptorRepository,
    private val applicationContext: ApplicationContext,
    @Value(value = "\${eventStore.publishingMode}")
    private val publishingMode: String
) {

    fun storeEvent(event: EventPublishing) {
        val eventDescriptor = EventDescriptor(event, applicationContext)
        eventdescriptorRepository.save(eventDescriptor)
    }

    fun markAsPublished(events: List<UUID>) {
        when(publishingMode) {
            "UPDATE" -> eventdescriptorRepository.markAsPublished(events)
            "DELETE" -> eventdescriptorRepository.deletePublished(events)
            else -> throw InvalidApplicationPropertyException("eventStore.publishingMode must either be UPDATE or DELETE")
        }
    }

    fun fetchUnpublishedEvents(): List<EventPublishing> {
        return eventdescriptorRepository.findByStatus(EventDescriptorStatus.CREATED)
            .map{ descriptor: EventDescriptor -> descriptor.getAsEventPublishing()}
    }
}