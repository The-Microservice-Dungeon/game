package microservice.dungeon.game.unittests.model.robot.service

import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.domain.RobotStatus
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import microservice.dungeon.game.aggregates.robot.services.RobotService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.argThat
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.whenever
import java.util.*

class RobotServiceTest {
    private var robotRepositoryMock: RobotRepository? = null
    private var robotService: RobotService? = null

    private val ROBOT_ID = UUID.randomUUID()
    private val PLAYER_ID = UUID.randomUUID()
    private val GAME_ID = UUID.randomUUID()

    private val ANY_ROBOT_ID = UUID.randomUUID()
    private val ANY_PLAYER_ID = UUID.randomUUID()
    private val ANY_GAME_ID = UUID.randomUUID()


    @BeforeEach
    fun setup() {
        robotRepositoryMock = mock(RobotRepository::class.java)
        robotService = RobotService(robotRepositoryMock!!)
    }


    @Test
    fun shouldAllowToCreateNewActiveRobot() {
        // when
        robotService!!.newRobot(ROBOT_ID, PLAYER_ID, GAME_ID)

        // then
        argumentCaptor<Robot>().apply {
            verify(robotRepositoryMock!!).save(capture())
            val newRobot = firstValue

            assertThat(newRobot.getRobotId())
                .isEqualTo(ROBOT_ID)
            assertThat(newRobot.getPlayerId())
                .isEqualTo(PLAYER_ID)
            assertThat(newRobot.getGameId())
                .isEqualTo(GAME_ID)
            assertThat(newRobot.getRobotStatus())
                .isEqualTo(RobotStatus.ACTIVE)
        }
    }

    @Test
    fun shouldNotAllowToCreateNewActiveRobotWhenSameRobotAlreadyExists() {
        // given
        whenever(robotRepositoryMock!!.findById(any()))
            .thenReturn(Optional.of(
                Robot(ROBOT_ID, ANY_PLAYER_ID, ANY_GAME_ID))
            )

        // when
        robotService!!.newRobot(ROBOT_ID, ANY_PLAYER_ID, ANY_GAME_ID)

        // then
        verify(robotRepositoryMock!!, never()).save(any())
    }


    @Test
    fun shouldAllowToMakeRobotInactive() {
        // given
        whenever(robotRepositoryMock!!.findById(any()))
            .thenReturn(Optional.of(
                Robot(ROBOT_ID, ANY_PLAYER_ID, ANY_GAME_ID))
            )

        // when
        robotService!!.destroyRobot(ROBOT_ID)

        // then
        verify(robotRepositoryMock!!).save(argThat { robot ->
            robot.getRobotStatus() == RobotStatus.INACTIVE
        })
    }

    @Test
    fun shouldNotSaveRobotWhenMakingRobotInactiveAndRobotDoesNotExist() {
        // given
        whenever(robotRepositoryMock!!.findById(any()))
            .thenReturn(Optional.empty())

        // when
        robotService!!.destroyRobot(ROBOT_ID)

        // then
        verify(robotRepositoryMock!!, never()).save(any())
    }
}