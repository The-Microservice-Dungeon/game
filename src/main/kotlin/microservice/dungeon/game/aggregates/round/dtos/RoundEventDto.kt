package microservice.dungeon.game.aggregates.round.dtos

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.*

class RoundEventDto constructor(
    val roundNumber: Int,
    val roundStatus: String
): EventDto {

    constructor(roundNumber: Int, roundStatus: RoundStatus):
            this(roundNumber, mapStatusToOutputStatus(roundStatus))

    override fun serialize(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }

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
    }
}