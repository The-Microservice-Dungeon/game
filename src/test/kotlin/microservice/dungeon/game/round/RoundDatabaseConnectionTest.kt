package microservice.dungeon.game.round

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RoundDatabaseConnectionTest @Autowired constructor(
    private val roundRepository: RoundRepository
) {

    @Test
    fun contextLoads() {
    }

    @Test
    fun testQueryReturn() {
        val roundIn = Round(1)
        roundRepository.save(roundIn)
        val roundOut: Round = roundRepository.findByRoundNumber(1).get()
    }
}