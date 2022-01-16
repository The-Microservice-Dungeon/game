package microservice.dungeon.game.aggregates.core

import com.fasterxml.jackson.annotation.JsonIgnore
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import java.time.LocalDateTime
import java.util.*


interface Event {
    fun getId(): UUID

    fun getTransactionId(): UUID

    fun getEventName(): String

    fun getVersion(): Int

    fun getOccurredAt(): EventTime

    fun serialized(): String

    fun getTopic(): String

    fun toDTO(): EventDto

    fun isSameAs(event: Event): Boolean
}