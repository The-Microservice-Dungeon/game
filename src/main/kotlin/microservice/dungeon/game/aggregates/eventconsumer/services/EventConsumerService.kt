package microservice.dungeon.game.aggregates.eventconsumer.services

import microservice.dungeon.game.aggregates.eventconsumer.domain.EventMessage
import microservice.dungeon.game.aggregates.eventconsumer.repositories.EventMessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class EventConsumerService @Autowired constructor(
    private val eventMessageRepository: EventMessageRepository
) {
    @Transactional
    fun consumeMessage(id: UUID, callback: () -> Unit) {
        if (eventMessageRepository.findById(id).isEmpty) {
            val eventMessage = EventMessage(id, LocalDateTime.now())
            eventMessageRepository.save(eventMessage)
            callback()
        }
    }
}