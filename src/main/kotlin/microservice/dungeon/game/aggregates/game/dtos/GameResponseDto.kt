package microservice.dungeon.game.aggregates.game.dtos

import microservice.dungeon.game.aggregates.game.domain.GameStatus
import java.time.LocalDateTime
import java.util.*

class GameResponseDto(
    val gameUUID: UUID?,
    private var gameStatus: GameStatus?,
    private var maxPlayers: Int,
    private var maxRounds: Int,
    private var roundDuration: Long?,
    private var commandCollectDuration: Double,
    private var createdGameDateTime: LocalDateTime?,
) {
}