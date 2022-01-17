package microservice.dungeon.game.messaging.consumer.robot.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
class RobotCreatedDto(
    val robotId: UUID,
    val playerId: UUID
) {
    companion object {
        private val objectMapper = ObjectMapper().findAndRegisterModules()

        fun makeFromSerialization(serial: String): RobotCreatedDto =
            objectMapper.readValue(serial, RobotCreatedDto::class.java)
    }

    fun serialize(): String {
        return objectMapper.writeValueAsString(this)
    }


    override fun equals(other: Any?): Boolean =
        (other is RobotCreatedDto)
                && robotId == other.robotId
                && playerId == other.playerId

    override fun toString(): String {
        return "RobotCreatedDto(robotId=$robotId, playerId=$playerId)"
    }
}