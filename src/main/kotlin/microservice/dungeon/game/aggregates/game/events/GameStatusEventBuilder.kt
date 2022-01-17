package microservice.dungeon.game.aggregates.game.events

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventBuilder
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class GameStatusEventBuilder @Autowired constructor(
    @Value("\${kafka.event.prod.gameStatus.topic}")
    private val topic: String,
    @Value("\${kafka.event.prod.gameStatus.type}")
    private val eventType: String,
    @Value("\${kafka.event.prod.gameStatus.version}")
    private val version: Int

) : EventBuilder {

    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }

    override fun deserializedEvent(serialized: String): Event {
        return objectMapper.readValue(serialized, GameStatusEvent::class.java)
    }

    fun makeGameStatusEvent(transactionId: UUID, gameId: UUID, gameStatus: GameStatus, ): GameStatusEvent {
        return GameStatusEvent(
            id = UUID.randomUUID(),
            transactionId = transactionId,
            occurredAt = EventTime.makeFromNow(),
            eventName = eventType,
            topic = topic,
            version = version,
            gameId = gameId,
            gameStatus = gameStatus
        )
    }
}