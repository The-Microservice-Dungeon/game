package microservice.dungeon.game.aggregates.game.events

import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import java.time.LocalDateTime
import java.util.*

class GameCreated(id: UUID,
                           occurredAt: EventTime,
                           gameId: UUID,
                           gameStatus: GameStatus) : AbstractGameEvent(id, occurredAt,  gameId,  gameStatus, "gameCreated", "status", 1) {


    constructor(game: Game) :
            this(
                UUID.randomUUID(),
                EventTime.makeFromLocalDateTime(LocalDateTime.now()),
                game.getGameId(),
                game.getGameStatus()
            )

}
