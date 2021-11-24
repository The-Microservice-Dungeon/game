package microservice.dungeon.game.integrationtests.model.round

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29098"
])
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29098", "port=29098"])
class RoundRepositoryIntegrationTest @Autowired constructor(
    private val roundRepository: RoundRepository,
    private val transactionTemplate: TransactionTemplate
) {
    @BeforeEach
    fun initialize() {
        roundRepository.deleteAll()
    }

    @Test
    fun saveRoundShouldPersistRound() {
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

        assertThat(loadedRound.getRoundId())
            .isEqualTo(roundId)
    }

    @Test
    fun findByGameIdAndRoundNumberShouldFindRound() {
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

        assertThat(loadedRound.getRoundId())
            .isEqualTo(roundId)
    }
}