package microservice.dungeon.game.unittests.messaging.consumer

import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.robot.services.RobotService
import microservice.dungeon.game.messaging.consumer.robot.KafkaRobotConsumer
import microservice.dungeon.game.messaging.consumer.robot.dtos.RobotDestroyedDto
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.*

class KafkaRobotConsumerTest {
    private var mockRobotService: RobotService? = null
    private var kafkaRobotConsumer: KafkaRobotConsumer? = null

    private val ROBOT_ID = UUID.randomUUID()
    private val PLAYER_ID = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        mockRobotService = mock()
        kafkaRobotConsumer = KafkaRobotConsumer(mockRobotService!!)
    }


    @Test
    fun shouldCreateNewRobotWhenReceivingValidRobotSpawnedMessage() {
        // given
        val validRecord = generateConsumerRecord(ROBOT_ID, PLAYER_ID)

        // when
        kafkaRobotConsumer!!.makeNewRobot(validRecord)

        // then
        verify(mockRobotService!!).newRobot(ROBOT_ID, PLAYER_ID)
    }

    @Test
    fun shouldDestroyRobotWhenReceivingValidRobotDestroyedMessage() {
        // given
        val validRecord = generateConsumerRecord(ROBOT_ID, PLAYER_ID)

        // when
        kafkaRobotConsumer!!.destroyRobot(validRecord)

        // then
        verify(mockRobotService!!).destroyRobot(ROBOT_ID)
    }



    private fun generateConsumerRecord(robotId: UUID, playerId: UUID): ConsumerRecord<String, String> {
        val dto = RobotDestroyedDto(robotId, playerId)
        val record = ConsumerRecord<String, String>("ANY-TOPIC", 0, 0L, null, dto.serialize())

        record.headers().add(
            RecordHeader("eventId", UUID.randomUUID().toString().toByteArray())
        )
        record.headers().add(
            RecordHeader("transactionId", UUID.randomUUID().toString().toByteArray())
        )
        record.headers().add(
            RecordHeader("version", 1.toString().toByteArray())
        )
        record.headers().add(
            RecordHeader("timestamp", EventTime.makeFromNow().getFormattedTime().toByteArray())
        )
        record.headers().add(
            RecordHeader("type", "ANY-EVENT-NAME".toByteArray())
        )
        return record
    }
}