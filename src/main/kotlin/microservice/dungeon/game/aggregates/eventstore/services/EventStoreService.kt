package microservice.dungeon.game.aggregates.eventstore.services

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.InvalidApplicationPropertyException
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptorStatus
import microservice.dungeon.game.aggregates.eventstore.repositories.EventDescriptorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
open class EventStoreService @Autowired constructor(
    private val eventDescriptorRepository: EventDescriptorRepository,
    private val applicationContext: ApplicationContext,
    private val environment: Environment,
    @Value(value = "\${eventStore.publishingMode}")
    private val publishingMode: String
) {

    fun storeEvent(event: Event) {
        if (!eventDescriptorRepository.findById(event.getId()).isEmpty) {
            throw EntityAlreadyExistsException("An Event with id ${event.getId()} already exists")
        }
        val eventDescriptor = EventDescriptor(event)
        eventDescriptorRepository.save(eventDescriptor)
    }

    @Transactional
    open fun markAsPublished(events: List<UUID>) {
        when(publishingMode) {
            "UPDATE" -> eventDescriptorRepository.markAsPublished(events)
            "DELETE" -> eventDescriptorRepository.deletePublished(events)
            else -> throw RuntimeException("eventStore.publishingMode must either be UPDATE or DELETE")
        }
    }

    @Transactional
    fun fetchUnpublishedEvents(): List<Event> {
        return eventDescriptorRepository.findByStatus(EventDescriptorStatus.CREATED)
            .map{ descriptor: EventDescriptor -> descriptor.getAsEvent(environment, applicationContext)}
    }
}