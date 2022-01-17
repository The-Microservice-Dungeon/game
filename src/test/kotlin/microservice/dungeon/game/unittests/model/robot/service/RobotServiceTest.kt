package microservice.dungeon.game.unittests.model.robot.service

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.domain.PlayerNotFoundException
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.domain.RobotAlreadyExistsException
import microservice.dungeon.game.aggregates.robot.domain.RobotNotFoundException
import microservice.dungeon.game.aggregates.robot.domain.RobotStatus
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import microservice.dungeon.game.aggregates.robot.services.RobotService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.mockito.kotlin.*
import java.util.*

class RobotServiceTest {
    private var mockRobotRepository: RobotRepository? = null
    private var mockPlayerRepository: PlayerRepository? = null

    private var robotService: RobotService? = null

    private var player: Player? = null

    @BeforeEach
    fun setup() {
        mockRobotRepository = mock()
        mockPlayerRepository = mock()
        robotService = RobotService(
            mockRobotRepository!!,
            mockPlayerRepository!!
        )
        player = Player("dadepu","dadepu@smail.th-koeln.de")
    }

    @Test
    fun shouldAllowToCreateNewActiveRobot() {
        // given
        val robotId = UUID.randomUUID()
        val playerId = player!!.getPlayerId()
        whenever(mockPlayerRepository!!.findById(playerId))
            .thenReturn(Optional.of(player!!))

        // when
        robotService!!.newRobot(robotId, playerId)

        // then
        verify(mockPlayerRepository!!).findById(playerId)
        verify(mockRobotRepository!!).save(check { robot: Robot ->
            assertThat(robot.getRobotId())
                .isEqualTo(robotId)
            assertThat(robot.getPlayer())
                .isEqualTo(player!!)
            assertThat(robot.getRobotStatus())
                .isEqualTo(RobotStatus.ACTIVE)
        })
    }

    @Test
    fun shouldThrowWhenRobotAlreadyExistsWhileCreatingNew() {
        // given
        val robotId = UUID.randomUUID()
        val playerId = player!!.getPlayerId()
        whenever(mockPlayerRepository!!.findById(playerId))
            .thenReturn(Optional.of(player!!))
        whenever(mockRobotRepository!!.existsById(robotId))
            .thenReturn(true)

        // when then
        assertThrows(RobotAlreadyExistsException::class.java) {
            robotService!!.newRobot(robotId, playerId)
        }

        // and then
        verify(mockRobotRepository!!).existsById(robotId)
    }

    @Test
    fun shouldThrowWhenPlayerNotFoundWhileCreatingNewRobot() {
        // given
        val robotId = UUID.randomUUID()
        val playerId = player!!.getPlayerId()
        whenever(mockPlayerRepository!!.findById(playerId))
            .thenReturn(Optional.empty())

        // when then
        assertThrows(PlayerNotFoundException::class.java) {
            robotService!!.newRobot(robotId, playerId)
        }

        // and then
        verify(mockPlayerRepository!!).findById(playerId)
    }

    @Test
    fun shouldAllowToMakeRobotInactive() {
        // given
        val robotId = UUID.randomUUID()
        val spyRobot: Robot = spy(Robot(robotId, mock()))
        whenever(mockRobotRepository!!.findById(robotId))
            .thenReturn(Optional.of(spyRobot))

        // when
        robotService!!.destroyRobot(robotId)

        // then
        verify(spyRobot).destroyRobot()
        verify(mockRobotRepository!!).save(check { robot: Robot ->
            assertThat(robot)
                .isEqualTo(spyRobot)
            assertThat(robot.getRobotStatus())
                .isEqualTo(RobotStatus.INACTIVE)
        })
    }

    @Test
    fun shouldThrowWhenRobotNotFoundWhileTryingToDestroy() {
        // given
        val robotId = UUID.randomUUID()

        // when
        assertThrows(RobotNotFoundException::class.java) {
            robotService!!.destroyRobot(robotId)
        }

        // then
        verify(mockRobotRepository!!).findById(robotId)
    }
}