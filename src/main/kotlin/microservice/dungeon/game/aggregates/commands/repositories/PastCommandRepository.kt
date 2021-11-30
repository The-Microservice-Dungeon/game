package microservice.dungeon.game.aggregates.commands.repositories

import microservice.dungeon.game.aggregates.commands.domain.RoundCommands
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoundCommandsRepository : JpaRepository<RoundCommands, Int>