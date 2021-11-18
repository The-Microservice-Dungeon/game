package microservice.dungeon.game.aggregates.round.events

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.dtos.RoundEventDto
import java.time.LocalDateTime
import java.util.*

abstract class AbstractRoundEvent constructor(
    private val id: UUID,
    private val occurredAt: LocalDateTime,
    private val roundId: UUID,
    private val gameId: UUID,
    private val roundNumber: Int,
    private val roundStatus: RoundStatus,
    private val eventName: String,
    private val topic: String
): Event  {

    override fun getId(): UUID = id

    override fun getOccurredAt(): LocalDateTime = occurredAt

    fun getRoundId(): UUID = roundId

    fun getGameId(): UUID = gameId

    fun getRoundNumber(): Int = roundNumber

    fun getRoundStatus(): RoundStatus = roundStatus

    override fun getEventName(): String = eventName

    override fun getTopic(): String = topic

    override fun serialized(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }

    override fun toDTO(): EventDto {
        return RoundEventDto(id, occurredAt, roundId, gameId, roundNumber, roundStatus)
    }

    fun equals(event: RoundStarted): Boolean =
        id == event.getId()
                && occurredAt == event.getOccurredAt()
                && roundId == event.getRoundId()
                && gameId == event.getGameId()
                && roundNumber == event.getRoundNumber()
                && roundStatus == event.getRoundStatus()
                && eventName == event.getEventName()

    override fun isSameAs(comparison: Event): Boolean =
         getId() == comparison.getId()
                && serialized() == comparison.serialized()
}