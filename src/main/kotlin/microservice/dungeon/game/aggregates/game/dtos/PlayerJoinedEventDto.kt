package microservice.dungeon.game.aggregates.game.dtos


import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import java.util.*


class PlayerJoinedEventDto(
    val userId: UUID,
    val lobbyAction: String = "joined"
): EventDto {
    constructor(playerId: UUID ): this(playerId,"joined")

    override fun serialize(): String {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }
}