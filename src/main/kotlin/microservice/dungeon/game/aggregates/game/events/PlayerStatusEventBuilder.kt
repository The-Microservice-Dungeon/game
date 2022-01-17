package microservice.dungeon.game.aggregates.game.events

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventBuilder
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class PlayerStatusEventBuilder @Autowired constructor(
    @Value("\${kafka.event.prod.playerStatus.topic}")
    private val topic: String,
    @Value("\${kafka.event.prod.playerStatus.type}")
    private val eventType: String,
    @Value("\${kafka.event.prod.playerStatus.version}")
    private val version: Int

) : EventBuilder {

    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }

    override fun deserializedEvent(serialized: String): Event {
        return objectMapper.readValue(serialized, PlayerStatusEvent::class.java)
    }

    fun makePlayerStatusEvent(transactionId: UUID, playerId: UUID, playerUsername: String): PlayerStatusEvent {
        return PlayerStatusEvent(
            id = UUID.randomUUID(),
            transactionId = transactionId,
            occurredAt = EventTime.makeFromNow(),
            eventName = eventType,
            topic = topic,
            version = version,
            playerId = playerId,
            playerUsername = playerUsername
        )
    }
}