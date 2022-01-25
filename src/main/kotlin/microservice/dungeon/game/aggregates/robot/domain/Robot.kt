package microservice.dungeon.game.aggregates.robot.domain

import microservice.dungeon.game.aggregates.player.domain.Player
import mu.KotlinLogging
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "robots"
)
class Robot constructor(
    @Id
    @Column(name="robot_id")
    @Type(type="uuid-char")
    private val robotId: UUID,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private val player: Player,

    @Column(name = "robot_status")
    private var robotStatus: RobotStatus = RobotStatus.ACTIVE
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun getRobotId(): UUID = robotId

    fun getPlayer(): Player = player

    fun getPlayerId(): UUID = player.getPlayerId()

    fun getRobotStatus(): RobotStatus = robotStatus

    fun destroyRobot() {
        robotStatus = RobotStatus.INACTIVE
        logger.trace("RobotStatus set to INACTIVE.")
    }
}