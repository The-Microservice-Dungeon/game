package microservice.dungeon.game.messaging.consumer.robot.events

import microservice.dungeon.game.aggregates.robot.services.RobotService

class RobotDestroyed constructor (
    private val robotService: RobotService,
    message: String
) : AbstractRobotEvent(message) {

    override fun getCallback(): () -> Unit = {
        robotService.destroyRobot(robotId)
    }
}