package microservice.dungeon.game.unittests.model.robot.service

import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.domain.RobotStatus
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import microservice.dungeon.game.aggregates.robot.services.RobotService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Assertions.assertEquals
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

class RobotServiceTests {
    private var robotRepositoryMock: RobotRepository? = null
    private var robotService: RobotService? = null


    @BeforeEach
    fun setup() {
        robotRepositoryMock = mock(RobotRepository::class.java)
        robotService = RobotService(robotRepositoryMock!!)
    }


    @Test
    fun newRobotShouldPersistNewRobot() {
        val validRobotId = UUID.randomUUID()
        val validPlayerId = UUID.randomUUID()
        val validGameId = UUID.randomUUID()
        robotService!!.newRobot(validRobotId, validPlayerId, validGameId)

        argumentCaptor<Robot>().apply {
            verify(robotRepositoryMock!!).save(capture())
            assertThat(firstValue.getRobotId())
                .isEqualTo(validRobotId)
            assertThat(firstValue.getPlayerId())
                .isEqualTo(validPlayerId)
            assertThat(firstValue.getGameId())
                .isEqualTo(validGameId)
            assertThat(firstValue.getRobotStatus())
                .isEqualTo(RobotStatus.ACTIVE)
        }
    }

    @Test
    fun newRobotShouldNotPersistNewRobotWhenRobotAlreadyExists() {
        val validRobotId = UUID.randomUUID()
        val validPlayerId = UUID.randomUUID()
        val validGameId = UUID.randomUUID()
        whenever(robotRepositoryMock!!.findById(validRobotId))
            .thenReturn(Optional.of(
                Robot(validRobotId, UUID.randomUUID(), UUID.randomUUID()))
            )

        robotService!!.newRobot(validRobotId, validPlayerId, validGameId)

        verify(robotRepositoryMock!!, never()).save(any(Robot::class.java))
    }


    @Test
    fun destroyRobotShouldMakeRobotInactive() {
        val validRobotId = UUID.randomUUID()
        whenever(robotRepositoryMock!!.findById(validRobotId))
            .thenReturn(Optional.of(
                Robot(validRobotId, UUID.randomUUID(), UUID.randomUUID()))
            )

        robotService!!.destroyRobot(validRobotId)

        verify(robotRepositoryMock!!).save(argThat { robot ->
            robot.getRobotStatus() == RobotStatus.INACTIVE
        })
    }

    @Test
    fun destroyRobotShouldNotPersistRobotWhenRobotNotExists() {
        val validRobotId = UUID.randomUUID()
        whenever(robotRepositoryMock!!.findById(any(UUID::class.java))).thenReturn(Optional.empty())

        robotService!!.destroyRobot(validRobotId)

        verify(robotRepositoryMock!!, never()).save(any(Robot::class.java))
    }

    @Test
    fun destroyRobotShouldNotThrowWhenRobotNotExists() {
        val validRobotId = UUID.randomUUID()
        whenever(robotRepositoryMock!!.findById(any(UUID::class.java))).thenReturn(Optional.empty())

        assertThatNoException().isThrownBy {
            robotService!!.destroyRobot(validRobotId)
        }
    }
}