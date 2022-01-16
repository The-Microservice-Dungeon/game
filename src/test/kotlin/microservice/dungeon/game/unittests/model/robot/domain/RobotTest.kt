package microservice.dungeon.game.unittests.model.robot.domain

import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.domain.RobotStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import java.util.*

class RobotTest {
    private var activeRobot = Robot(UUID.randomUUID(), mock())


    @Test
    fun shouldInitializeAsActiveRobot() {
        assertThat(activeRobot.getRobotStatus())
            .isEqualTo(RobotStatus.ACTIVE)
    }

    @Test
    fun shouldAllowToMakeRobotInactive() {
        // when
        activeRobot.destroyRobot()

        // then
        assertThat(activeRobot.getRobotStatus())
            .isEqualTo(RobotStatus.INACTIVE)
    }
}