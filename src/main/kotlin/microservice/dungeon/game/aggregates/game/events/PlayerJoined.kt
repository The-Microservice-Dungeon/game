package microservice.dungeon.game.aggregates.game.events

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.domain.PlayersInGame
import microservice.dungeon.game.aggregates.player.domain.Player
import java.time.LocalDateTime
import java.util.*

class PlayerJoined (
    id: UUID,
    occurredAt: EventTime,
    gameId: UUID,
    playerId: UUID
): AbstractPlayerJoinedEvent(id, occurredAt,  gameId,  playerId, "playerJoined", "playerStatus", 1){

    constructor(game: Game, player: Player):
            this(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()),  game.getGameId(), player.getPlayerId())



}