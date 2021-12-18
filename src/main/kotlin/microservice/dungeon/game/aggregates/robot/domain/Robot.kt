package microservice.dungeon.game.aggregates.robot.domain

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Robot constructor(
    @Id
    @Type(type="uuid-char")
    private val robotId: UUID,
    @Type(type="uuid-char")
    private val playerId: UUID,
    private var robotStatus: RobotStatus = RobotStatus.ACTIVE
) {
    fun getRobotId(): UUID = robotId

    fun getPlayerId(): UUID = playerId

    fun getRobotStatus(): RobotStatus = robotStatus

    fun destroyRobot() {
        robotStatus = RobotStatus.INACTIVE
    }
}