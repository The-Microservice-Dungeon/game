package microservice.dungeon.game.aggregates.eventconsumer.domain

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "inbox")
class EventMessage constructor(
    @Id
    private val id: UUID,
    private val receivedAt: LocalDateTime
) {
    fun getId(): UUID = id

    fun getReceivedAt(): LocalDateTime = receivedAt
}