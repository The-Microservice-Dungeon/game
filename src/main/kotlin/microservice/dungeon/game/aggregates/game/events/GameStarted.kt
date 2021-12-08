package microservice.dungeon.game.aggregates.game.events


import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import java.time.LocalDateTime
import java.util.*

class GameStarted(
    id: UUID,
    occurredAt: EventTime,
    gameId: UUID,
    gameStatus: GameStatus
) : AbstractGameEvent(id, occurredAt,  gameId,  gameStatus, "gameStarted", "testTopic", 1) {

    constructor(game: Game):
            this(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()),  game.getGameId(), game.getGameStatus())

    init {
        if (gameStatus != GameStatus.CREATED)
            throw MethodNotAllowedForStatusException("GameStarted cannot created with game.status = $gameStatus")
    }

    override fun getTransactionId(): UUID {
        TODO("Not yet implemented")
    }
}