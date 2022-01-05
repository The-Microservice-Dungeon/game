package microservice.dungeon.game.aggregates.game.dtos


import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import java.lang.RuntimeException
import java.util.*

class PlayerJoinedEventDto(
    val playerId: UUID,
    val lobbyAction: String
): EventDto {
    constructor(playerId: UUID ):
            this(playerId,"joined")

    override fun serialize(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }

}