package microservice.dungeon.game.integrationtests.model.round.repository

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
    private val gameRepository: GameRepository,
    private val roundRepository: RoundRepository,
    private val transactionTemplate: TransactionTemplate
) {
    @BeforeEach
    fun initialize() {
        roundRepository.deleteAll()
    }

    @Test
    fun saveRoundShouldPersistRound() {
        val game = Game(10, 100)
        gameRepository.save(game)
        val roundNumber = 3
        val round = Round(game = game, roundNumber = roundNumber, roundStatus = RoundStatus.COMMAND_INPUT_STARTED)

        val roundId = transactionTemplate.execute {
            roundRepository.save(round)
            round.getRoundId()
        }!!
        val loadedRound = transactionTemplate.execute {
            roundRepository.findById(roundId).get()
        }!!

        assertThat(loadedRound.getRoundId())
            .isEqualTo(roundId)
        assertThat(loadedRound.getRoundStarted())
            .isEqualTo(round.getRoundStarted())
    }

    @Test
    fun findByGameIdAndRoundNumberShouldFindRound() {
        assertTrue(false)
    }
//        val game = Game(10, 100)
//        val gameId = game.getGameId()
//        val roundNumber = 3
//        val round = Round(game = game, roundNumber = roundNumber, roundStatus = RoundStatus.COMMAND_INPUT_STARTED)
//
//        val roundId = transactionTemplate.execute {
//            roundRepository.save(round)
//            round.getRoundId()
//        }!!
//        val loadedRound = transactionTemplate.execute {
//            roundRepository.findByGameIdAndRoundNumber(gameId, roundNumber).get()
//        }!!
//
//        assertThat(loadedRound.getRoundId())
//            .isEqualTo(roundId)
//    }
}