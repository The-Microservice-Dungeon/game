package microservice.dungeon.game.aggregates.round.repositories

import microservice.dungeon.game.aggregates.round.domain.Round
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RoundRepository : CrudRepository<Round, UUID> {

    fun findByGameIdAndRoundNumber(gameId: UUID, roundNumber: Int): Optional<Round>
}