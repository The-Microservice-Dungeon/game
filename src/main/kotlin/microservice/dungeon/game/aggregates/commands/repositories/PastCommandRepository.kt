package microservice.dungeon.game.aggregates.commands.repositories

import microservice.dungeon.game.aggregates.commands.domain.Command
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PastCommandRepository : JpaRepository<List<Command>, Int>