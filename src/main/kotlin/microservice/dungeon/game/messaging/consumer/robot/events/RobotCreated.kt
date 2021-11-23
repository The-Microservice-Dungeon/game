package microservice.dungeon.game.messaging.consumer.robot.events

import microservice.dungeon.game.aggregates.robot.services.RobotService
import java.time.LocalDateTime
import java.util.*

class RobotCreated constructor (
    private val robotService: RobotService,
    message: String
) : AbstractRobotEvent(message) {

    override fun getCallback(): () -> Unit = {
        robotService.newRobot(robotId, playerId, gameId)
    }
}