package microservice.dungeon.game.unittests.model.robot.domain

import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.domain.RobotStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class RobotTest {
    private var ANY_ROBOT = Robot(UUID.randomUUID(), UUID.randomUUID())


    @Test
    fun shouldInitializeAsActiveRobot() {
        assertThat(ANY_ROBOT.getRobotStatus())
            .isEqualTo(RobotStatus.ACTIVE)
    }

    @Test
    fun shouldAllowToMakeRobotInactive() {
        ANY_ROBOT.destroyRobot()

        assertThat(ANY_ROBOT.getRobotStatus())
            .isEqualTo(RobotStatus.INACTIVE)
    }
}