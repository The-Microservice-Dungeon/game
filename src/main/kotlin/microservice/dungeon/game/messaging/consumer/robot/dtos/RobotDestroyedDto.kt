package microservice.dungeon.game.messaging.consumer.robot.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
class RobotDestroyedDto(
    val robotId: UUID,
    val playerId: UUID
) {
    companion object {
        private val objectMapper = ObjectMapper().findAndRegisterModules()

        fun makeFromSerialization(serial: String): RobotDestroyedDto =
            objectMapper.readValue(serial, RobotDestroyedDto::class.java)
    }

    fun serialize(): String {
        return objectMapper.writeValueAsString(this)
    }


    override fun equals(other: Any?): Boolean =
        (other is RobotDestroyedDto)
                && robotId == other.robotId
                && playerId == other.playerId

    override fun toString(): String {
        return "RobotDestroyedDto(robotId=$robotId, playerId=$playerId)"
    }
}