package microservice.dungeon.game.aggregates.round.events.dto

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import java.lang.RuntimeException
import java.util.*

class RoundStatusEventDto (
    val roundId: UUID,
    val roundNumber: Int,
    val roundStatus: String

): EventDto {

    constructor(roundId: UUID, roundNumber: Int, roundStatus: RoundStatus):
            this(roundId, roundNumber, mapStatusToOutputStatus(roundStatus))

    companion object {
        private fun mapStatusToOutputStatus(status: RoundStatus): String {
            return when (status) {
                RoundStatus.COMMAND_INPUT_STARTED   -> "started"
                RoundStatus.COMMAND_INPUT_ENDED     -> "command input ended"
                RoundStatus.ROUND_ENDED             -> "ended"
                else -> {
                    throw RuntimeException("invalid roundStatus -> event api status mapping")
                }
            }
        }

        private val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }

    override fun serialize(): String {
        return objectMapper.writeValueAsString(this)
    }
}