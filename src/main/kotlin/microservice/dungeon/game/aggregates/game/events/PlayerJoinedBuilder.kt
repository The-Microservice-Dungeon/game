package microservice.dungeon.game.aggregates.game.events

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventBuilder
import microservice.dungeon.game.aggregates.game.events.PlayerJoined
import org.springframework.stereotype.Component


@Component
class PlayerJoinedBuilder: EventBuilder {
    override fun deserializedEvent(serialized: String): Event {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.readValue(serialized, PlayerJoined::class.java)
    }
}