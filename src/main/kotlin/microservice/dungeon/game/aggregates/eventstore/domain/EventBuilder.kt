package microservice.dungeon.game.aggregates.eventstore.domain

import microservice.dungeon.game.aggregates.core.Event

interface EventBuilder {
    fun deserializedEvent(serialized: String): Event
}