package microservice.dungeon.game.integrationtests.model.round.services

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandPayload
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.events.RoundStatusEventBuilder
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
import microservice.dungeon.game.aggregates.round.web.TradingCommandDispatcherClient
import microservice.dungeon.game.aggregates.round.web.dto.BlockCommandDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29102"
])
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29102", "port=29102"])
class RoundServiceIntegrationTest @Autowired constructor(
    private val roundRepository: RoundRepository,
    private val robotRepository: RobotRepository,
    private val commandRepository: CommandRepository,
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private var roundStatusEventBuilder: RoundStatusEventBuilder
) {
    private var roundService: RoundService? = null

    private var mockEventStoreService: EventStoreService? = null
    private var mockEventPublisherService: EventPublisherService? = null
    private var mockRobotCommandDispatcherClient: RobotCommandDispatcherClient? = null
    private var mockTradingCommandDispatcherClient: TradingCommandDispatcherClient? = null

    private var player1: Player? = null
    private var robot1: Robot? = null
    private var robot2: Robot? = null
    private var robot3: Robot? = null
    private var game1: Game? = null
    private var command1: Command? = null
    private var command2: Command? = null
    private var command3: Command? = null
    private var command4: Command? = null

    private var currentRound: Round? = null

    @BeforeEach
    fun setUp() {
        commandRepository.deleteAll()
        robotRepository.deleteAll()
        roundRepository.deleteAll()
        playerRepository.deleteAll()
        gameRepository.deleteAll()

        mockEventStoreService = mock()
        mockEventPublisherService = mock()
        mockRobotCommandDispatcherClient = mock()
        mockTradingCommandDispatcherClient = mock()

        roundService = RoundService(
            roundRepository,
            commandRepository,
            mockEventStoreService!!,
            gameRepository,
            mockEventPublisherService!!,
            mockRobotCommandDispatcherClient!!,
            mockTradingCommandDispatcherClient!!,
            roundStatusEventBuilder
        )

        player1 = Player("dadepu", "dadepu@smail.th-koeln.de")
        playerRepository.save(player1!!)

        robot1 = Robot(UUID.randomUUID(), player1!!)
        robot2 = Robot(UUID.randomUUID(), player1!!)
        robot3 = Robot(UUID.randomUUID(), player1!!)
        robotRepository.saveAll(listOf(robot1!!, robot2!!, robot3!!))

        game1 = Game(2, 10)
        game1!!.joinGame(player1!!)
        game1!!.startGame()
        game1!!.startNewRound()
        currentRound = game1!!.getCurrentRound()!!
        gameRepository.save(game1!!)

        command1 = Command(UUID.randomUUID(), currentRound!!, player1!!, robot1!!, CommandType.BLOCKING, CommandPayload(null, null, null, null))
        command2 = Command(UUID.randomUUID(), currentRound!!, player1!!, robot2!!, CommandType.BLOCKING, CommandPayload(null, null, null, null))
        command3 = Command(UUID.randomUUID(), currentRound!!, player1!!, null, CommandType.BUYING, CommandPayload(null, null, "ROBOT", 1))
        command4 = Command(UUID.randomUUID(), currentRound!!, player1!!, robot3, CommandType.MINING, CommandPayload(UUID.randomUUID(), null, null, null))
        commandRepository.saveAll(listOf(command1!!, command2!!, command3!!, command4!!))
    }

    @Test
    fun shouldFetchBlockingCommandsWhileDeliveringBlockingCommands() {
        // given
        currentRound!!.endCommandInputPhase()
        roundRepository.save(currentRound!!)

        // when
        roundService!!.deliverBlockingCommands(currentRound!!.getRoundId())

        // then
        verify(mockRobotCommandDispatcherClient!!).sendBlockingCommands(check { dtos: List<BlockCommandDto> ->
            assertThat(dtos)
                .hasSize(2)
            assertThat(dtos.map { it.robotId} )
                .contains(command1!!.getRobot()!!.getRobotId())
                .contains(command2!!.getRobot()!!.getRobotId())
        })
    }
}