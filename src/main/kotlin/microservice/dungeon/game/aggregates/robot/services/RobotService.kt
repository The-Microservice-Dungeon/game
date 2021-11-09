package microservice.dungeon.game.aggregates.robot.services

import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class RobotService @Autowired constructor(
    private val robotRepository: RobotRepository
) {

    fun newRobot(robotId: UUID, playerId: UUID, gameId: UUID) {
        if (robotRepository.findById(robotId).isEmpty()) {
            val robot = Robot(robotId, playerId, gameId)
            robotRepository.save(robot)
        }
    }

    fun destroyRobot(robotId: UUID) {
        try {
            val robot = robotRepository.findById(robotId).get()
            robot.destroyRobot()
            robotRepository.save(robot)
        } catch (e: Exception) {}
    }
}