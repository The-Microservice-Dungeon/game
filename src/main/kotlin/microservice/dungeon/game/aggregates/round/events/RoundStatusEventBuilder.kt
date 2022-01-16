package microservice.dungeon.game.aggregates.round.events

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventBuilder
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class RoundStatusEventBuilder @Autowired constructor(
    @Value("\${kafka.event.prod.roundStatus.topic}")
    private val topic: String,
    @Value("\${kafka.event.prod.roundStatus.type}")
    private val eventType: String,
    @Value("\${kafka.event.prod.roundStatus.version}")
    private val version: Int

) : EventBuilder {

    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }

    override fun deserializedEvent(serialized: String): Event {
        return objectMapper.readValue(serialized, RoundStatusEvent::class.java)
    }

    fun makeRoundStatusEvent(transactionId: UUID, roundId: UUID, roundNumber: Int, roundStatus: RoundStatus): RoundStatusEvent {
        return RoundStatusEvent(
            id = UUID.randomUUID(),
            transactionId = transactionId,
            occurredAt = EventTime.makeFromNow(),
            eventName = eventType,
            topic = topic,
            version = version,
            roundId = roundId,
            roundNumber = roundNumber,
            roundStatus = roundStatus
        )
    }
}