package microservice.dungeon.game.aggregates.core

import java.time.Instant
import java.time.LocalDateTime
import java.util.*


interface Event {
    fun getId(): UUID

    fun getEventName(): String

    fun getOccurredAt(): LocalDateTime

    fun serialized(): String

    fun toDTO(): EventDto

    fun getTopic(): String
}