package microservice.dungeon.game.aggregates.game.events

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.dtos.GameEventDto
import java.time.LocalDateTime
import java.util.*

abstract class AbstractGameEvent constructor(
    private val id: UUID,
    private val occurredAt: LocalDateTime,
    private val gameId: UUID,
    private val gameStatus: GameStatus,
    private val eventName: String,
    private val topic: String
): Event  {

    override fun getId(): UUID = id
    override fun getOccurredAt(): LocalDateTime = occurredAt

    fun getGameId(): UUID = gameId
    fun getGameStatus(): GameStatus = gameStatus

    override fun getEventName(): String = eventName
    override fun getTopic(): String = topic

    override fun serialized(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }

    override fun toDTO(): EventDto {
        return GameEventDto(id, occurredAt,  gameId, gameStatus)
    }

    fun equals(event: GameStarted): Boolean =
        id == event.getId()
                && occurredAt == event.getOccurredAt()
                && gameId == event.getGameId()
                && eventName == event.getEventName()
}