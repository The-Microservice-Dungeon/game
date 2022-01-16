package microservice.dungeon.game.integrationtests.model.round.repository

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29098"
])
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29098", "port=29098"])
class RoundRepositoryIntegrationTest @Autowired constructor(
    private val gameRepository: GameRepository,
    private val roundRepository: RoundRepository
) {
    private val game = Game(10, 100)

    @BeforeEach
    fun initialize() {
        roundRepository.deleteAll()
    }

    @Test
    fun shouldAllowToSaveAndFetchRounds() {
        // given
        val round = Round(game = game, roundNumber = 3, roundStatus = RoundStatus.COMMAND_INPUT_STARTED)
        gameRepository.save(game)
        roundRepository.save(round)

        // when
        val capturedRound: Round = roundRepository.findById(round.getRoundId()).get()

        // then
        assertThat(capturedRound.isEqualByVal(round))
            .isTrue
    }
}