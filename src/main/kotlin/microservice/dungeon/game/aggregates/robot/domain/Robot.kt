package microservice.dungeon.game.aggregates.robot.domain

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Robot constructor(
    @Id
    private val robotId: UUID,
    private val playerId: UUID,
    private val gameId: UUID,
    private var robotStatus: RobotStatus = RobotStatus.ACTIVE
) {
    fun getRobotId(): UUID = robotId

    fun getPlayerId(): UUID = playerId

    fun getGameId(): UUID = gameId

    fun getRobotStatus(): RobotStatus = robotStatus

    fun destroyRobot() {
        robotStatus = RobotStatus.INACTIVE
    }
}