package microservice.dungeon.game.aggregates.game.events

import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import java.time.LocalDateTime
import java.util.*

class GameEnded (
    id: UUID,
    occurredAt: EventTime,
    gameId: UUID,
    gameStatus: GameStatus
) : AbstractGameEvent(id, occurredAt,  gameId,  gameStatus, "gameEnded", "testTopic", 1) {

    constructor(game: Game):
            this(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()),  game.getGameId(),  game.getGameStatus())

    init {
        if (gameStatus != GameStatus.GAME_FINISHED)
            throw MethodNotAllowedForStatusException("GameEnd cannot created with game.status = $gameStatus")
    }

    override fun getTransactionId(): UUID {
        TODO("Not yet implemented")
    }


}