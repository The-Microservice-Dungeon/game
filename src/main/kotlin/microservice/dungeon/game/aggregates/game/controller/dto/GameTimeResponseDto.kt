package microservice.dungeon.game.aggregates.game.controller.dto

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.game.domain.Game

class GameTimeResponseDto (
    val gameTime: Long?,          // Elapsed time since game-start (in Seconds)
    val roundCount: Int?,        // Number of current round
    val roundTime: Long?          // Time elapsed since round-start (in Seconds)
) {
     constructor(game: Game): this(
         game.getTimeSinceGameStartInSeconds(),
         game.getCurrentRound()?.getRoundNumber(),
         game.getCurrentRound()?.getTimeSinceRoundStartInSeconds()
    )

    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }
    fun serialize(): String {
        return objectMapper.writeValueAsString(this)
    }
}