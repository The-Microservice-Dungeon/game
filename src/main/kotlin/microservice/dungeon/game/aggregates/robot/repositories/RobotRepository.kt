package microservice.dungeon.game.aggregates.robot.repositories

import microservice.dungeon.game.aggregates.robot.domain.Robot
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RobotRepository: CrudRepository<Robot, UUID> {
}