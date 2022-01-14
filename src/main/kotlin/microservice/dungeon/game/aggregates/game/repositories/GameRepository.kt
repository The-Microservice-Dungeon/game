package microservice.dungeon.game.aggregates.game.repositories

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GameRepository : CrudRepository<Game, UUID> {

    fun existsByGameStatusIn(gameStatus: List<GameStatus>): Boolean
}
