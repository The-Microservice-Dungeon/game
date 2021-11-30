package microservice.dungeon.game.commands.repository

import microservice.dungeon.game.commands.domain.Command
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommandRepository : JpaRepository<Command, UUID>