package microservice.dungeon.game.aggregates.game.repositories

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.PlayersInGame
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GameRepository : CrudRepository<Game, UUID> {

    fun findByGameId(gameId: UUID): Optional<Game>
    abstract fun save(player: PlayersInGame)


}
