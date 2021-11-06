package microservice.dungeon.game.aggregates.round.events

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import java.time.LocalDateTime
import java.util.*

class RoundEnded (
    private val id: UUID,
    private val occurredAt: LocalDateTime,
    private val roundNumber: Int,
    private val roundStatus: RoundStatus
) : Event {
    constructor(occurredAt: LocalDateTime, roundNumber: Int, roundStatus: RoundStatus):
            this(UUID.randomUUID(), occurredAt, roundNumber, roundStatus)

    private val topic: String = "testTopic"
    private val eventName: String = "roundEnded"

    override fun getId(): UUID = id

    override fun getEventName(): String = eventName

    override fun getOccurredAt(): LocalDateTime = occurredAt

    override fun serialized(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        var rootNode: JsonNode = objectMapper.valueToTree(this)
        (rootNode as ObjectNode).remove("topic")
        return objectMapper.writeValueAsString(rootNode)
    }

    override fun getTopic(): String = topic

    fun getRoundNumber(): Int = roundNumber

    fun getRoundStatus(): RoundStatus = roundStatus
}