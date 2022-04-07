package microservice.dungeon.game.integrationtests.model.command.controller

import microservice.dungeon.game.aggregates.command.controller.CommandController
import microservice.dungeon.game.aggregates.command.controller.dto.CommandObjectRequestDto
import microservice.dungeon.game.aggregates.command.controller.dto.CommandRequestDto
import microservice.dungeon.game.aggregates.command.controller.dto.RoundCommandsResponseDto
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.command.services.CommandService
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.services.GameService
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.player.services.PlayerService
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import microservice.dungeon.game.aggregates.robot.services.RobotService
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29104"
])
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29104", "port=29104"])
class GetAllRoundCommandsIntegrationTest @Autowired constructor(
    private val commandController: CommandController,
    private val commandService: CommandService,
    private val playerService: PlayerService,
    private val gameService: GameService,
    private val robotService: RobotService,
    private val commandRepository: CommandRepository,
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val robotRepository: RobotRepository,
    private val roundRepository: RoundRepository,
){
    private var playerTokenId: UUID? = null
    private var playerId: UUID? = null
    private var gameId: UUID? = null
    private var robotId: UUID? = null

    @BeforeEach
    fun setUp() {
        val player = playerService.createNewPlayer("dadepu", "dadepu@smail.th-koeln.de")
        playerTokenId = player.getPlayerToken()
        playerId = player.getPlayerId()
        robotId = UUID.randomUUID()
        robotService.newRobot(robotId!!, playerId!!)
        gameId = gameService.createNewGame(1, 1).second.getGameId()
        gameService.joinGame(playerTokenId!!, gameId!!)
        gameService.startGame(gameId!!)
    }

    @AfterEach
    fun cleanUp() {
        commandRepository.deleteAll()
        robotRepository.deleteAll()
        roundRepository.deleteAll()
        gameRepository.deleteAll()
        playerRepository.deleteAll()
    }

    @Test
    fun shouldRetrieveCommandsWithEmptyPayload() {
        // given
        commandService.createNewCommand(gameId!!, playerTokenId!!, robotId!!, CommandType.MINING, CommandRequestDto(
            gameId!!, playerTokenId!!, robotId!!, "any", CommandObjectRequestDto(
                "any", null, null, null, null
            )
        ))

        // when
        val response: ResponseEntity<RoundCommandsResponseDto> = commandController.getAllRoundCommands(gameId!!, 1)

        // then
        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)
    }
}
