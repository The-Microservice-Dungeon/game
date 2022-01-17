package microservice.dungeon.game.aggregates.game.events.dto

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import java.util.*

class GameStatusEventDto(
    val gameId: UUID,
    val status: String

) : EventDto {

    constructor(gameId: UUID, gameStatus: GameStatus): this (
        gameId, mapGameStatusToSpecification(gameStatus)
    )

    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()

        fun mapGameStatusToSpecification(status: GameStatus): String {
            return when (status) {
                GameStatus.CREATED -> "created"
                GameStatus.GAME_RUNNING -> "started"
                GameStatus.GAME_FINISHED -> "ended"
            }
        }
    }

    override fun serialize(): String {
        return objectMapper.writeValueAsString(this)
    }
}