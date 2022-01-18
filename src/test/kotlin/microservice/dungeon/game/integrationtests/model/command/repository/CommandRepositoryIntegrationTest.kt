package microservice.dungeon.game.integrationtests.model.command.repository

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandPayload
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.util.*


@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29103"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29103", "port=29103"])
class CommandRepositoryIntegrationTest @Autowired constructor (
    private val commandRepository: CommandRepository,
    private val roundRepository: RoundRepository,
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository
) {
    private var game1: Game? = null
    private var round1: Round? = null
    private var round2: Round? = null
    private var command1: Command? = null
    private var command2: Command? = null
    private var command3: Command? = null
    private var player1: Player? = null

    @BeforeEach
    fun setUp() {
        commandRepository.deleteAll()
        roundRepository.deleteAll()
        gameRepository.deleteAll()

        player1 = Player("dadepu", "dadepu@smail.th-koeln.de")
        game1 = Game(10,10)
        game1!!.startGame()
        round1 = game1!!.getCurrentRound()
        game1!!.startNewRound()
        round2 = game1!!.getCurrentRound()!!
        command1 = Command(UUID.randomUUID(), round1!!, player1!!, null, CommandType.BUYING, CommandPayload(null, null, "ROBOT", 1))
        command2 = Command(UUID.randomUUID(), round2!!, player1!!, null, CommandType.BUYING, CommandPayload(null, null, "ROBOT", 1))
        command3 = Command(UUID.randomUUID(), round2!!, player1!!, null, CommandType.BUYING, CommandPayload(null, null, "ROBOT", 1))

        playerRepository.save(player1!!)
        gameRepository.save(game1!!)
        commandRepository.saveAll(listOf(command1, command2, command3))
    }

    @Test
    // WORST WORKAROUND EVER
    fun shouldAllowToFetchCommandsByRoundNumberAndGame() {
        // given
        val gameId: UUID = game1!!.getGameId()
        val round1Number: Int = round1!!.getRoundNumber()
        val round2Number: Int = round2!!.getRoundNumber()

        // when
        val game: Game = gameRepository.findById(gameId).get()
        val round1: Round = roundRepository.findRoundByGameAndRoundNumber(game, round1Number).get()
        val fetchedCommands1: List<Command> = commandRepository.findAllCommandsByRound(round1)

        // then
        assertThat(fetchedCommands1)
            .hasSize(1)
        assertThat(fetchedCommands1.map { it.getCommandId() })
            .contains(command1!!.getCommandId())

        // and when
        val round2: Round = roundRepository.findRoundByGameAndRoundNumber(game, round2Number).get()
        val fetchedCommands2: List<Command> = commandRepository.findAllCommandsByRound(round2)

        // then
        assertThat(fetchedCommands2)
            .hasSize(2)
        assertThat(fetchedCommands2.map { it.getCommandId() })
            .contains(command2!!.getCommandId())
            .contains(command3!!.getCommandId())
    }

//    @Test
//    fun shouldAllowToFetchCommandsByRoundNumberAndGameId() {
//        // given
//        val gameId: UUID = game1!!.getGameId()
//        val roundNumber1: Int = this.round1!!.getRoundNumber()
//        val roundNumber2: Int = this.round2!!.getRoundNumber()
//
//        // when
//        val fetchedRound1: List<Command> = commandRepository.findAllCommandsByGameIdAndRoundNumber()
//
//        println(fetchedRound1)
//
//        // then
//        assertThat(fetchedRound1)
//            .hasSize(1)
//        assertThat(fetchedRound1.map { it.getCommandId() })
//            .contains(command1!!.getCommandId())
//
////        // and when
////        val fetchedRound2: List<Command> = commandRepository.findAllCommandsByGameIdAndRoundNumber(gameId)
////
////        // then
////        assertThat(fetchedRound2)
////            .hasSize(2)
////        assertThat(fetchedRound2.map { it.getCommandId() })
////            .contains(command2!!.getCommandId())
////            .contains(command3!!.getCommandId())
//    }
}