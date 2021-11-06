package microservice.dungeon.game.aggregates.eventstore.domain

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventBuilder
import org.hibernate.annotations.Type
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "outbox")
class EventDescriptor constructor(
    @Transient
    private val event: Event
) {
    @Id
    @Type(type = "uuid-char")
    private val id: UUID = event.getId()

    private val type: String = event.getEventName()

    private val occurredAt: LocalDateTime = event.getOccurredAt()

    private val content: String = event.serialized()

    @Enumerated(value = EnumType.STRING)
    private val status: EventDescriptorStatus = EventDescriptorStatus.CREATED


    fun getAsEvent(environment: Environment, applicationContext: ApplicationContext): Event {
        val eventBuilderSuffix: String = environment.getProperty("eventStore.builderSuffix").toString()
        val eventBuilder: Any = applicationContext.getBean("${type}${eventBuilderSuffix}")
        if (eventBuilder !is EventBuilder) {
            throw EventTypeMissMatchException("No matching EventBuilder found for type ${type} with suffix ${eventBuilderSuffix}")
        }
        return eventBuilder.deserializedEvent(content)
    }


    fun getId(): UUID = id

    fun getType(): String = type

    fun getOccurredAt(): LocalDateTime = occurredAt

    fun getContent(): String = content

    fun getStatus(): EventDescriptorStatus = status
}