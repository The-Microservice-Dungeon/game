package microservice.dungeon.game.aggregates.player.events

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventBuilder
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import org.springframework.stereotype.Component

@Component
class PlayerCreatedBuilder: EventBuilder {

    override fun deserializedEvent(serialized: String): Event {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.readValue(serialized, PlayerCreated::class.java)
    }
}