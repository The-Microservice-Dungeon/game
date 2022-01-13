package microservice.dungeon.game.messaging.consumer.robot

import microservice.dungeon.game.aggregates.robot.services.RobotService
import microservice.dungeon.game.messaging.consumer.robot.dtos.RobotCreatedDto
import microservice.dungeon.game.messaging.consumer.robot.dtos.RobotDestroyedDto
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaRobotConsumer @Autowired constructor(
    private val robotService: RobotService
) {
    private val logger = KotlinLogging.logger {}
    @KafkaListener(id = "\${kafka.topicSubRobotSpawned.group}", topics = ["\${kafka.topicSubRobotSpawned}"])
    fun makeNewRobot(record: ConsumerRecord<String, String>) {
        try {
            val payload = RobotCreatedDto.makeFromSerialization(record.value())
            logger.debug("Received new RobotSpawnEvent.")
            logger.trace(payload.toString())
            robotService.newRobot(payload.robotId, payload.playerId)
        } catch (e: Exception) {
            logger.error("Failed to consume RobotSpawnEvent.")
            logger.error(e.message ?: "")
            logger.error(record.value())
        }
    }

    @KafkaListener(id = "\${kafka.topicSubRobotDestroyed.group}", topics = ["\${kafka.topicSubRobotDestroyed}"])
    fun destroyRobot(record: ConsumerRecord<String, String>) {
        try {
            val payload = RobotDestroyedDto.makeFromSerialization(record.value())
            logger.debug("Received new RobotDestroyedEvent.")
            logger.trace(payload.toString())
            robotService.destroyRobot(payload.robotId)
        } catch (e: Exception){
            logger.error("Failed to consume RobotDestroyedEvent.")
            logger.error(e.message ?: "")
            logger.error(record.value())
        }
    }
}