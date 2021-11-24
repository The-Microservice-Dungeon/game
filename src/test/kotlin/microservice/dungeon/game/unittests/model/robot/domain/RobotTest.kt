package microservice.dungeon.game.unittests.model.robot.domain

import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.domain.RobotStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RobotTest {
    private var validRobot: Robot? = null

    @BeforeEach
    fun setUp() {
        validRobot = Robot(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
    }

    @Test
    fun newRobotShouldBeInitialized() {
        assertThat(validRobot!!.getRobotStatus())
            .isEqualTo(RobotStatus.ACTIVE)
    }

    @Test
    fun destroyRobotShouldMakeRobotInactive() {
        validRobot!!.destroyRobot()

        assertThat(validRobot!!.getRobotStatus())
            .isEqualTo(RobotStatus.INACTIVE)
    }
}