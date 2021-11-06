package microservice.dungeon.game.aggregates.core

import microservice.dungeon.game.aggregates.core.Event

interface EventBuilder {
    fun deserializedEvent(serialized: String): Event
}