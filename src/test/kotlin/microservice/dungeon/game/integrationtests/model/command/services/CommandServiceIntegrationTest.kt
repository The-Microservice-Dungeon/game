package microservice.dungeon.game.integrationtests.model.command.services

import microservice.dungeon.game.aggregates.command.controller.dto.CommandObjectRequestDto
import microservice.dungeon.game.aggregates.command.controller.dto.CommandRequestDto
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandPayload
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.command.services.CommandService
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
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29101"
])
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29101", "port=29101"])
class CommandServiceIntegrationTest @Autowired constructor(
    private val commandService: CommandService,
    private val commandRepository: CommandRepository,
    private val playerRepository: PlayerRepository,
    private val roundRepository: RoundRepository,
    private val gameRepository: GameRepository
){
    private var player: Player? = null
    private var game: Game? = null
    private var round: Round? = null

    @BeforeEach
    fun setUp() {
        commandRepository.deleteAll()
        playerRepository.deleteAll()
        roundRepository.deleteAll()
        gameRepository.deleteAll()

        player = Player("dadepu", "dadepu@smail.th-koeln.de")
        playerRepository.save(player!!)

        game = Game(1,10)
        game!!.joinGame(player!!)
        game!!.startGame()
        game!!.startNewRound()
        round = game!!.getCurrentRound()
        gameRepository.save(game!!)
    }

    @Test
    fun shouldPersistCommandWhenCreatingNewCommand() {
        // given
        val requestBody = CommandRequestDto(
            gameId = game!!.getGameId(), playerToken = player!!.getPlayerToken(), null, "selling",
            CommandObjectRequestDto(
                "selling", null, null, "ROBOT", 1
            )
        )

        // when
        val commandId: UUID = commandService.createNewCommand(
            requestBody.gameId, requestBody.playerToken, null, CommandType.SELLING, requestBody
        )

        // then
        val command: Command = commandRepository.findById(commandId).get()
        assertThat(command.getCommandType())
            .isEqualTo(CommandType.SELLING)
    }

    @Test
    fun shouldDeleteDuplicatePlayerCommandsForSameRobotAndRoundWhenCreatingNewCommand() {
        // given
        val previousCommand = Command(UUID.randomUUID(), round!!, player!!, null, CommandType.SELLING,
            CommandPayload(null, null, "ROBOT", 1)
        )
        commandRepository.save(previousCommand)

        val newCommandRequest = CommandRequestDto(
            gameId = game!!.getGameId(), playerToken = player!!.getPlayerToken(), null, "selling",
            CommandObjectRequestDto(
                "selling", null, null, "ROBOT", 1
            )
        )

        // when
        val commandId: UUID = commandService.createNewCommand(
            newCommandRequest.gameId, newCommandRequest.playerToken, null, CommandType.SELLING, newCommandRequest
        )

        // then
        val existsPreviousCommand = commandRepository.existsById(previousCommand.getCommandId())
        val existsNewCommand = commandRepository.existsById(commandId)

        assertThat(existsPreviousCommand)
            .isFalse
        assertThat(existsNewCommand)
            .isTrue
    }

    // create new commands
    // remove duplicates
    // find commands by id
}