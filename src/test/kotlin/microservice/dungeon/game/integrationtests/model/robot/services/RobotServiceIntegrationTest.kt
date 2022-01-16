package microservice.dungeon.game.integrationtests.model.robot.services

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.robot.domain.RobotStatus
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import microservice.dungeon.game.aggregates.robot.services.RobotService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29097"
])
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29097", "port=29097"])
class RobotServiceIntegrationTest @Autowired constructor(
    private val robotRepository: RobotRepository,
    private val playerRepository: PlayerRepository,
    private val robotService: RobotService
) {
    private var player: Player? = null

    @BeforeEach
    fun setUp() {
        robotRepository.deleteAll()
        playerRepository.deleteAll()
        player = Player("dadepu", "dadepu@smail.th-koeln.de")
    }

    @Test
    fun shouldPersistRobotWhenCreatingNewOne() {
        // given
        val robotId = UUID.randomUUID()
        val playerId = player!!.getPlayerId()
        playerRepository.save(player!!)

        // when
        robotService.newRobot(robotId, playerId)

        // then
        val capturedRobot = robotRepository.findById(robotId).get()
        assertThat(capturedRobot.getPlayerId())
            .isEqualTo(playerId)
        assertThat(capturedRobot.getRobotStatus())
            .isEqualTo(RobotStatus.ACTIVE)
    }
}