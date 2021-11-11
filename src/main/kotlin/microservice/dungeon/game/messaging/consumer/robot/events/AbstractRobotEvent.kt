package microservice.dungeon.game.messaging.consumer.robot.events

import java.time.LocalDateTime
import java.util.*

abstract class AbstractRobotEvent constructor(
    message: String
) {
    protected val eventId: UUID
    protected val robotId: UUID
    protected val gameId: UUID
    protected val playerId: UUID
    protected val occurredAt: LocalDateTime

    init {
        //TODO
        eventId = UUID.randomUUID()
        robotId = UUID.randomUUID()
        gameId = UUID.randomUUID()
        playerId = UUID.randomUUID()
        occurredAt = LocalDateTime.now()
    }

    abstract fun getCallback(): () -> Unit
}