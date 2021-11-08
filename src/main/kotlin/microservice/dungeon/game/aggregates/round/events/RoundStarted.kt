package microservice.dungeon.game.aggregates.round.events

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.dtos.RoundEventDto
import java.time.LocalDateTime
import java.util.*

class RoundStarted(
    private val id: UUID,
    private val occurredAt: LocalDateTime,
    private val roundId: UUID,
    private val gameId: UUID,
    private val roundNumber: Int,
    private val roundStatus: RoundStatus
) : Event {
    constructor(occurredAt: LocalDateTime, roundId: UUID, gameId: UUID, roundNumber: Int, roundStatus: RoundStatus):
            this(UUID.randomUUID(), occurredAt, roundId, gameId, roundNumber, roundStatus)

    private val topic: String = "testTopic"
    private val eventName: String = "roundStarted"

    override fun getId(): UUID = id

    override fun getEventName(): String = eventName

    override fun getOccurredAt(): LocalDateTime = occurredAt

    override fun serialized(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }

    override fun toDTO(): EventDto {
        return RoundEventDto(id, occurredAt, roundId, gameId, roundNumber, roundStatus)
    }

    override fun getTopic(): String = topic

    fun getRoundId(): UUID = roundId

    fun getGameId(): UUID = gameId

    fun getRoundNumber(): Int = roundNumber

    fun getRoundStatus(): RoundStatus = roundStatus
}