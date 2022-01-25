package microservice.dungeon.game.aggregates.round.repositories

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.round.domain.Round
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RoundRepository : CrudRepository<Round, UUID> {

    fun findRoundByGameAndRoundNumber(game: Game, roundNumber: Int): Optional<Round>

    fun findRoundByGame_GameIdAndRoundNumber(gameId: UUID, roundNumber: Int): Optional<Round>
}