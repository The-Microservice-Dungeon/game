package microservice.dungeon.game.integrationtests.messaging.consumer

import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.domain.RobotStatus
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import microservice.dungeon.game.messaging.consumer.robot.dtos.RobotCreatedDto
import microservice.dungeon.game.messaging.consumer.robot.dtos.RobotDestroyedDto
import microservice.dungeon.game.messaging.producer.KafkaProducing
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.util.*
import org.junit.jupiter.api.Disabled

@Disabled
@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29099"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29099", "port=29099"])
class KafkaRobotConsumerIntegrationTest @Autowired constructor(
    private val kafkaProducer: KafkaProducing,
    private val robotRepository: RobotRepository,
    private val playerRepository: PlayerRepository,

    @Value("\${kafka.topicSubRobotSpawned}") val robotSpawnedTopic: String,
    @Value("\${kafka.topicSubRobotDestroyed}") val robotDestroyedTopic: String
) {

    @BeforeEach
    fun setUp() {
        robotRepository.deleteAll()
        playerRepository.deleteAll()
    }

    @Test
    fun shouldConsumeRobotSpawnedEvent() {
        // given
        val player = Player("dadepu", "dadepu@smail.th-koeln.de")
        val robotId = UUID.randomUUID()
        val record: ProducerRecord<String, String> = createProducerRecord(
            robotSpawnedTopic, RobotCreatedDto(robotId, player.getPlayerId()).serialize(), UUID.randomUUID(), "anyType"
        )
        playerRepository.save(player)

        // when
        kafkaProducer.send(record)
        Thread.sleep(200)

        // then
        assertThat(robotRepository.existsById(robotId))
            .isTrue
    }

    @Test
    // FAILS IF EXECUTED IN BULK, reason unknown
    fun shouldConsumeRobotDestroyedEvent() {
        // given
        val player = Player("dadepu", "dadepu@smail.th-koeln.de")
        val robot = Robot(UUID.randomUUID(), player, RobotStatus.ACTIVE)
        val record: ProducerRecord<String, String> = createProducerRecord(
            robotDestroyedTopic, RobotDestroyedDto(robot.getRobotId(), player.getPlayerId()).serialize(), UUID.randomUUID(), "anyType"
        )
        playerRepository.save(player)
        robotRepository.save(robot)

        // when
        kafkaProducer.send(record)
        Thread.sleep(300)

        // then
        val capturedRobot: Robot = robotRepository.findById(robot.getRobotId()).get()
        assertThat(capturedRobot.getRobotStatus())
            .isEqualTo(RobotStatus.INACTIVE)
    }

    private fun createProducerRecord(topic: String, payload: String, transactionId: UUID, eventType: String): ProducerRecord<String, String> {
        val record = ProducerRecord<String, String>(topic, payload)
        record.headers().add(
            RecordHeader("eventId", UUID.randomUUID().toString().toByteArray())
        )
        record.headers().add(
            RecordHeader("transactionId", transactionId.toString().toByteArray())
        )
        record.headers().add(
            RecordHeader("version", 1.toString().toByteArray())
        )
        record.headers().add(
            RecordHeader("timestamp", EventTime.makeFromNow().getFormattedTime().toByteArray())
        )
        record.headers().add(
            RecordHeader("type", eventType.toByteArray())
        )
        return record
    }
}