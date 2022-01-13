package microservice.dungeon.game.aggregates.game.repositories

import microservice.dungeon.game.aggregates.game.domain.Game
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GameRepository : CrudRepository<Game, UUID> {

}
