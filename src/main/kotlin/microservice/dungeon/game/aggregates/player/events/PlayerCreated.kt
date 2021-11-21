package microservice.dungeon.game.aggregates.player.events

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.core.EventDto
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.player.domain.Player
import java.time.LocalDateTime
import java.util.*

class PlayerCreated (
    eventId: UUID,
    transactionId: UUID,
    occurredAt: EventTime,
    player: Player
): AbstractPlayerEvent(
    eventId,
    transactionId,
    occurredAt,
    "player-created",
    "player",
    1,
    player.getPlayerId(),
    player.getUserName(),
    player.getMailAddress()
) {
    constructor(player: Player):
        this(UUID.randomUUID(), player.getPlayerId(), EventTime.makeFromLocalDateTime(LocalDateTime.now()), player)
}