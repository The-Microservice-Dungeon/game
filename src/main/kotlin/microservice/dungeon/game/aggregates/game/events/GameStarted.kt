package microservice.dungeon.game.aggregates.game.events


import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import java.time.LocalDateTime
import java.util.*

class GameStarted(
    id: UUID,
    occurredAt: LocalDateTime,
    gameId: UUID,
    gameStatus: GameStatus
) : AbstractGameEvent(id, occurredAt,  gameId,  gameStatus, "gameStarted", "testTopic") {

    constructor(game: Game):
            this(UUID.randomUUID(), LocalDateTime.now(),  game.getGameId(), game.getGameStatus())

    init {
        if (gameStatus != GameStatus.IN_PREPARATION)
            throw MethodNotAllowedForStatusException("RoundStarted cannot created with round.status = $gameStatus")
    }
}