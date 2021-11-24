package microservice.dungeon.game.integrationtests.model.robot

import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import microservice.dungeon.game.aggregates.robot.services.RobotService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29097"
])
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29097", "port=29097"])
class RobotRepositoryIntegrationTest @Autowired constructor(
    private val robotRepository: RobotRepository,
    private val transactionTemplate: TransactionTemplate
) {
    @BeforeEach
    fun initialize() {
        robotRepository.deleteAll()
    }

    @Test
    @Transactional
    fun saveRobotShouldPersistRobot() {
        val robotId = UUID.randomUUID()
        val playerId = UUID.randomUUID()
        val gameId = UUID.randomUUID()
        val robot = Robot(robotId, playerId, gameId)

        transactionTemplate.execute {
            robotRepository.save(robot)
        }
        val loadedRobot = transactionTemplate.execute {
            robotRepository.findById(robotId).get()
        }!!

        assertThat(loadedRobot.getRobotId())
            .isEqualTo(robot.getRobotId())
        assertThat(loadedRobot.getRobotStatus())
            .isEqualTo(robot.getRobotStatus())
    }
}