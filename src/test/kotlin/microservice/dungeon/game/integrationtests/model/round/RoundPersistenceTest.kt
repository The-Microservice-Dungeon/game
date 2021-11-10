package microservice.dungeon.game.integrationtests.model.round

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.util.*

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29092", "port=29092"])
class RoundPersistenceTest @Autowired constructor(
    private val roundRepository: RoundRepository,
    private val transactionTemplate: TransactionTemplate
) {
    @BeforeEach
    fun initialize() {
        roundRepository.deleteAll()
    }

    @Test
    fun saveRoundAndFindTest() {
        val gameId = UUID.randomUUID()
        val roundNumber = 3
        val round = Round(gameId, roundNumber)

        val roundId = transactionTemplate.execute {
            roundRepository.save(round)
            round.getRoundId()
        }!!
        val loadedRound = transactionTemplate.execute {
            roundRepository.findById(roundId).get()
        }!!
        assertEquals(loadedRound.getRoundId(), roundId)
    }

    @Test
    fun loadRoundWithGameIdAndRoundNumber() {
        val gameId = UUID.randomUUID()
        val roundNumber = 3
        val round = Round(gameId, roundNumber)

        val roundId = transactionTemplate.execute {
            roundRepository.save(round)
            round.getRoundId()
        }!!
        val loadedRound = transactionTemplate.execute {
            roundRepository.findByGameIdAndRoundNumber(gameId, roundNumber).get()
        }!!
        assertEquals(loadedRound.getRoundId(), roundId)
    }
}