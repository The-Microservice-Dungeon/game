package microservice.dungeon.game.unittests.messaging.consumer

import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.robot.services.RobotService
import microservice.dungeon.game.messaging.consumer.robot.KafkaRobotConsumer
import microservice.dungeon.game.messaging.consumer.robot.dtos.RobotDestroyedDto
import org.apache.kafka.clients.producer.ProducerRecord
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
    fun shouldDestroyRobotWhenReceivingValidMessage() {
        // given
        val validRecord = makeValidProducerRecord(ROBOT_ID, PLAYER_ID)

        // when
        kafkaRobotConsumer!!.destroyRobot(validRecord)

        // then
        verify(mockRobotService!!).destroyRobot(ROBOT_ID)
    }



    private fun makeValidProducerRecord(robotId: UUID, playerId: UUID): ProducerRecord<String, String> {
        val dto = RobotDestroyedDto(robotId, playerId)
        val record = ProducerRecord<String, String>("ANY-TOPIC", dto.serialize())

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