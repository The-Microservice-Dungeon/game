package microservice.dungeon.game.integrationtests.model.game.repository

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29095"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29095", "port=29095"])
class GameRepositoryIntegrationTest @Autowired constructor(
    private val gameRepository: GameRepository
) {

    @BeforeEach
    fun setUp() {
        gameRepository.deleteAll()
    }

    @Test
    fun shouldAllowToSaveAndFetchGames() {
        // given
        val newGame: Game = Game(10, 100)

        // when
        gameRepository.save(newGame)

        // then
        val fetchedGame: Game = gameRepository.findById(newGame.getGameId()).get()
    }

    @Test
    fun shouldFindExistingGameWhenCreatedGameExists() {
        // given
        val newGame: Game = Game(10, 100)
        gameRepository.save(newGame)

        // when
        val response: Boolean = gameRepository.existsByGameStatusIn(listOf(GameStatus.CREATED, GameStatus.GAME_RUNNING))

        // then
        assertTrue(response)
    }

    @Test
    fun shouldFindNoExistingGameWhenFinishedGameExists() {
        // given
        val newGame: Game = Game(10, 100)
        newGame.endGame()
        gameRepository.save(newGame)

        // when
        val response: Boolean = gameRepository.existsByGameStatusIn(listOf(GameStatus.CREATED, GameStatus.GAME_RUNNING))

        // then
        assertFalse(response)
    }
}