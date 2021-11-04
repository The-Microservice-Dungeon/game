package microservice.dungeon.game.aggregates.core

import java.time.Instant
import java.util.*


interface Event {
    fun getId(): UUID

    fun getEventName(): String

    fun getOccurredAt(): Instant
}