package microservice.dungeon.game.aggregates.player.events

import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.player.domain.Player
import java.time.LocalDateTime
import java.util.*

class PlayerCreated (
    id: UUID,
    occurredAt: EventTime,
    playerId: UUID,
    userName: String,
    mailAddress: String
): AbstractPlayerEvent(
    id,
    playerId,
    occurredAt,
    "player-created",
    "player",
    1,
    playerId,
    userName,
    mailAddress
) {
    constructor(player: Player):
        this(
            UUID.randomUUID(),
            EventTime.makeFromLocalDateTime(LocalDateTime.now()),
            player.getPlayerId(),
            player.getUserName(),
            player.getMailAddress()
        )
}