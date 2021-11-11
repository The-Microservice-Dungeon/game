package microservice.dungeon.game.aggregates.eventconsumer.domain

import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "inbox")
class EventMessage constructor(
    @Id
    @Type(type="uuid-char")
    private val id: UUID,
    iOccurredAt: LocalDateTime,
    iReceivedAt: LocalDateTime
) {
    private val occurredAt: LocalDateTime = iOccurredAt.truncatedTo(ChronoUnit.SECONDS);
    private val receivedAt: LocalDateTime = iReceivedAt.truncatedTo(ChronoUnit.SECONDS);

    fun getId(): UUID = id

    fun getOccurredAt(): LocalDateTime = occurredAt

    fun getReceivedAt(): LocalDateTime = receivedAt

    fun equalsValue(b: EventMessage): Boolean {
        return id == b.getId() &&
                    occurredAt == b.getOccurredAt() &&
                    receivedAt == b.getReceivedAt()
    }
}