package microservice.dungeon.game.aggregates.round.repositories

import microservice.dungeon.game.aggregates.round.domain.Round
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RoundRepository : CrudRepository<Round, UUID> {

    // TODO(check nullable or throwable return value)
    fun findByRoundNumber(roundNumber: Int): Round
}