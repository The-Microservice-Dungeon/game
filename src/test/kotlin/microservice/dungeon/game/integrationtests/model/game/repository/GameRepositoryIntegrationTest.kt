package microservice.dungeon.game.integrationtests.model.game.repository

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.util.*

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
        assertThat(fetchedGame
            .isEqualByVal(newGame)).isTrue
    }

    @Test
    fun shouldCascadeAllOnRoundsWhenSaving() {
        // given
        val newGame: Game = Game(10, 100)
        newGame.startGame()

        // when
        gameRepository.save(newGame)

        // then
        val fetchedGame: Game = gameRepository.findById(newGame.getGameId()).get()
        val fetchedActiveRound: Round = fetchedGame.getCurrentRound()!!

        assertThat(fetchedGame
            .isEqualByVal(newGame)).isTrue
        assertThat(fetchedActiveRound
            .isEqualByVal(newGame.getCurrentRound()!!)).isTrue
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

    @Test
    fun shouldFindGamesByStatusIn() {
        // given
        val createdGame = Game(UUID.randomUUID(), GameStatus.CREATED, 1, 1, 1, 1, mutableSetOf(), mutableSetOf())
        val startedGame = Game(UUID.randomUUID(), GameStatus.CREATED, 1, 1, 1, 1, mutableSetOf(), mutableSetOf())
        startedGame.startGame()
        val finishedGame = Game(UUID.randomUUID(), GameStatus.CREATED, 1, 1, 1, 1, mutableSetOf(), mutableSetOf())
        finishedGame.startGame()
        finishedGame.endGame()
        gameRepository.saveAll(listOf(createdGame, startedGame, finishedGame))

        // when
        val fetchedGames: List<UUID> = gameRepository.findAllByGameStatusIn(
            listOf(GameStatus.CREATED, GameStatus.GAME_RUNNING)
        ).map { game -> game.getGameId()}

        // then
        assertThat(fetchedGames)
            .contains(createdGame.getGameId())
        assertThat(fetchedGames)
            .contains(startedGame.getGameId())
        assertThat(fetchedGames)
            .doesNotContain(finishedGame.getGameId())
    }
}