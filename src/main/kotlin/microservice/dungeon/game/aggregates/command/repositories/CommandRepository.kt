package microservice.dungeon.game.aggregates.command.repositories

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.round.domain.Round
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommandRepository : JpaRepository<Command, UUID> {

    fun findAllCommandsByRoundAndCommandType(round: Round, commandType: CommandType): List<Command>
}