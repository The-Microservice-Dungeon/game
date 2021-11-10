package microservice.dungeon.game.integrationtests.model.robot

import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import microservice.dungeon.game.aggregates.robot.services.RobotService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.util.*

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29092", "port=29092"])
class RobotPersistenceTest @Autowired constructor(
    private val robotRepository: RobotRepository,
    private val transactionTemplate: TransactionTemplate
) {
    @BeforeEach
    fun initialize() {
        robotRepository.deleteAll()
    }

    @Test
    @Transactional
    fun saveRobotAndFindTest() {
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
        assertEquals(loadedRobot.getRobotId(), robot.getRobotId())
        assertEquals(loadedRobot.getRobotStatus(), robot.getRobotStatus())
    }
}