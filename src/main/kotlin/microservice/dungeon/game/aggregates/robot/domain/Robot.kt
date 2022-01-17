package microservice.dungeon.game.aggregates.robot.domain

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.player.domain.Player
import mu.KotlinLogging
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
class Robot constructor(
    @Id
    @Column(name="ROBOT_ID")
    @Type(type="uuid-char")
    private val robotId: UUID,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PLAYER_ID")
    private val player: Player,

    @Column(name = "ROBOT_STATUS")
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