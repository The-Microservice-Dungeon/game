package microservice.dungeon.game.messaging.consumer.robot.dtos

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

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
        val objectMapper = ObjectMapper().findAndRegisterModules()
        return objectMapper.writeValueAsString(this)
    }

    override fun equals(other: Any?): Boolean =
        (other is RobotDestroyedDto)
                && robotId == other.robotId
                && playerId == other.playerId
}