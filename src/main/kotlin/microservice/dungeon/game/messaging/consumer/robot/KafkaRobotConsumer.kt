package microservice.dungeon.game.messaging.consumer.robot

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.robot.services.RobotService
import microservice.dungeon.game.messaging.consumer.robot.dtos.RobotDestroyedDto
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaRobotConsumer @Autowired constructor(
    private val robotService: RobotService
) {
    @KafkaListener(id = "\${robot-destroyed-listener}", topics = ["\${kafka.topicSubRobotDestroyed}"])
    fun destroyRobot(record: ProducerRecord<String, String>) {
        try {
            val payload = RobotDestroyedDto.makeFromSerialization(record.value())
            robotService.destroyRobot(payload.robotId)
        } catch (e: Exception){
            //TODO("log any errors")
        }
    }
}