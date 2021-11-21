package microservice.dungeon.game.aggregates.player.events

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.dtos.PlayerEventDto
import microservice.dungeon.game.aggregates.round.dtos.RoundEventDto
import java.util.*

abstract class AbstractPlayerEvent (
    private val eventId: UUID,
    private val transactionId: UUID,
    private val occurredAt: EventTime,
    private val eventName: String,
    private val topic: String,
    private val version: Int,
    private val playerId: UUID,
    private val userName: String,
    private val mailAddress: String
): Event {
    override fun getId(): UUID = eventId

    override fun getTransactionId(): UUID = transactionId

    override fun getOccurredAt(): EventTime = occurredAt

    override fun getEventName(): String = eventName

    override fun getTopic(): String = topic

    override fun getVersion(): Int = version

    fun getPlayerId(): UUID = playerId

    fun getUserName(): String = userName

    fun getMailAddress(): String = mailAddress

    override fun serialized(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }

    override fun toDTO(): EventDto = PlayerEventDto(playerId, userName, mailAddress)

    override fun isSameAs(event: Event): Boolean =
        getId() == event.getId()
                && getTransactionId() == event.getTransactionId()
                && getOccurredAt() == event.getOccurredAt()
                && getEventName() == event.getEventName()
                && getTopic() == event.getTopic()
                && getVersion() == event.getVersion()
}