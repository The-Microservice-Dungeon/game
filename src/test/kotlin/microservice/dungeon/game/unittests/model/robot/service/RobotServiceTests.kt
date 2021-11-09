package microservice.dungeon.game.unittests.model.robot.service

import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.domain.RobotStatus
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import microservice.dungeon.game.aggregates.robot.services.RobotService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.isA
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
    fun makeNewRobotTest() {
        val robotId = UUID.randomUUID()
        val playerId = UUID.randomUUID()
        val gameId = UUID.randomUUID()
        robotService!!.newRobot(robotId, playerId, gameId)

        val robotArg = argumentCaptor<Robot>()
        verify(robotRepositoryMock!!).save(robotArg.capture())
        assertEquals(robotArg.firstValue.getRobotId(), robotId)
        assertEquals(robotArg.firstValue.getPlayerId(), playerId)
        assertEquals(robotArg.firstValue.getGameId(), gameId)
        assertEquals(robotArg.firstValue.getRobotStatus(), RobotStatus.ACTIVE)
    }

    @Test
    fun makeNewRobotWhenAlreadyExistsTest() {
        val robotId = UUID.randomUUID()
        val playerId = UUID.randomUUID()
        val gameId = UUID.randomUUID()
        whenever(robotRepositoryMock!!.findById(robotId)).thenReturn(Optional.of(Robot(robotId, UUID.randomUUID(), UUID.randomUUID())))

        robotService!!.newRobot(robotId, playerId, gameId)

        verify(robotRepositoryMock!!, never()).save(any(Robot::class.java))
    }

    @Test
    fun destroyRobotTest() {
        val robotId = UUID.randomUUID()
        whenever(robotRepositoryMock!!.findById(robotId)).thenReturn(Optional.of(Robot(robotId, UUID.randomUUID(), UUID.randomUUID())))

        robotService!!.destroyRobot(robotId)
        verify(robotRepositoryMock!!).save(argThat { robot ->
            robot.getRobotStatus() == RobotStatus.INACTIVE
        })
    }

    @Test
    fun destroyRobotWhenNotExistsTest() {
        val robotId = UUID.randomUUID()
        whenever(robotRepositoryMock!!.findById(robotId)).thenReturn(Optional.empty())

        robotService!!.destroyRobot(robotId)

        verify(robotRepositoryMock!!, never()).save(any(Robot::class.java))
    }
}