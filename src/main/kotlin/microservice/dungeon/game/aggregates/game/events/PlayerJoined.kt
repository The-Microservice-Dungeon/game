package microservice.dungeon.game.aggregates.game.events

import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.player.domain.Player
import java.time.LocalDateTime
import java.util.*

//class PlayerJoined (
//    id: UUID,
//    occurredAt: EventTime,
//    gameId: UUID,
//    playerId: UUID,
//    transactionId: UUID
//): AbstractPlayerJoinedEvent(id, occurredAt,  gameId,  playerId, "playerJoined", "playerStatus", 1, transactionId){
//
//    constructor(game: Game, player: Player, transactionId: UUID):
//            this(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()),  game.getGameId(), player.getPlayerId(), transactionId)
//
//
//
//}