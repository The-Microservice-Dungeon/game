package microservice.dungeon.game.aggregates.eventstore.domain

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventPublishing
import org.hibernate.annotations.Type
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "outbox")
class EventDescriptor constructor(
    @Transient
    private val event: EventPublishing,
    @Transient
    private val applicationConext: ApplicationContext
) {
    @Id
    @Type(type = "uuid-char")
    val id: UUID = UUID.randomUUID()

    val type: String = event.getEventName()

    val occurredAt: Instant = event.getOccurredAt()

    val content: String = event.serializeEvent()

    @Enumerated(value = EnumType.STRING)
    val status: EventDescriptorStatus = EventDescriptorStatus.CREATED


    fun getAsEventPublishing(): EventPublishing {
        try {
            val event: Any = applicationConext.getBean(type)
        } catch (e: BeansException) {
            throw EventTypeMissMatchException("No matching Event-Bean could be found with name $type")
        }
        if (event !is EventPublishing) {
            throw EventTypeMissMatchException("Bean $type does not conform to type Event")
        }
        event.initializeFromSerializedEvent(content)
        return event
    }
}