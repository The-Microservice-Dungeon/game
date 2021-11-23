package microservice.dungeon.game.messaging.consumer.robot

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.eventconsumer.services.EventConsumerService
import microservice.dungeon.game.aggregates.robot.services.RobotService
import microservice.dungeon.game.messaging.consumer.robot.events.AbstractRobotEvent
import microservice.dungeon.game.messaging.consumer.robot.events.RobotCreated
import microservice.dungeon.game.messaging.consumer.robot.events.RobotDestroyed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class KafkaRobotConsumer @Autowired constructor(
    private val eventConsumerService: EventConsumerService,
    private val robotService: RobotService
) {
    @KafkaListener(topics = ["\${kafka.topicSubRobot}"])
    fun consume(message: String) {
        // read message
        //TODO
    }

    fun buildEvent(type: RobotEventType, message: String): AbstractRobotEvent {
        return when (type) {
            RobotEventType.ROBOT_CREATED -> RobotCreated(robotService, message)
            RobotEventType.ROBOT_DESTROYED -> RobotDestroyed(robotService, message)
        }
    }

    enum class RobotEventType {
        ROBOT_CREATED,
        ROBOT_DESTROYED
    }
}