package microservice.dungeon.game.aggregates.robot.services

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.domain.PlayerNotFoundException
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.domain.RobotAlreadyExistsException
import microservice.dungeon.game.aggregates.robot.domain.RobotNotFoundException
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RobotService @Autowired constructor(
    private val robotRepository: RobotRepository,
    private val playerRepository: PlayerRepository
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun newRobot(robotId: UUID, playerId: UUID) {
        val transactionId = UUID.randomUUID()
        val player: Player

        if (robotRepository.existsById(robotId)) {
            logger.warn("Failed to create new robot. Robot does already exist. [robotId=$robotId]")
            throw RobotAlreadyExistsException("Failed to create new robot. Robot does already exist.")
        }

        try {
            player = playerRepository.findById(playerId).get()
        } catch (e: Exception) {
            logger.error("Failed to create new robot. Player not found! [playerId=$playerId]")
            throw PlayerNotFoundException("Failed to create new Robot. Player not found.")
        }

        val newRobot = Robot(robotId, player)
        robotRepository.save(newRobot)
        logger.info("Robot created. [robotId=${robotId}, playerName=${player.getUserName()}]")
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun destroyRobot(robotId: UUID) {
        val transactionId = UUID.randomUUID()
        val robot: Robot

        try {
            robot = robotRepository.findById(robotId).get()
        } catch (e: Exception) {
            logger.error("Failed to destroy robot. Robot not found. [robotId=$robotId]")
            throw RobotNotFoundException("Failed to destroy robot. Robot not found.")
        }

        robot.destroyRobot()
        robotRepository.save(robot)
        logger.info("Robot destroyed. [robotId=$robotId]")
    }
}