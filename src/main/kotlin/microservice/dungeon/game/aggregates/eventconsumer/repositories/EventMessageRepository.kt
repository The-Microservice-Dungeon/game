package microservice.dungeon.game.aggregates.eventconsumer.repositories

import microservice.dungeon.game.aggregates.eventconsumer.domain.EventMessage
import org.springframework.data.repository.CrudRepository
import java.util.*

interface EventMessageRepository: CrudRepository<EventMessage, UUID> {
}