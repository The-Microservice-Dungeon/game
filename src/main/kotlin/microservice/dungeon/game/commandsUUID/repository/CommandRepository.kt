package microservice.dungeon.game.commandsUUID.repository

import microservice.dungeon.game.commandsUUID.model.Command
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommandRepository : JpaRepository<Command, Int>