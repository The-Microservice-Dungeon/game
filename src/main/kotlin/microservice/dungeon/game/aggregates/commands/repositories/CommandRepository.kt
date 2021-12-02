package microservice.dungeon.game.aggregates.commands.repositories

import microservice.dungeon.game.aggregates.commands.domain.Command
import microservice.dungeon.game.aggregates.commands.domain.CommandType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommandRepository : JpaRepository<Command, UUID> {
    fun findByRobotIdIn(robotId: UUID): List<Command>
    fun findByCommandTypeIn(commandType: CommandType): List<Command>
}