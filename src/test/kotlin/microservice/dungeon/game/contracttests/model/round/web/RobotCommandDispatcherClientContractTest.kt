package microservice.dungeon.game.contracttests.model.round.web

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandObject
import microservice.dungeon.game.aggregates.command.dtos.BlockCommandDTO
import microservice.dungeon.game.aggregates.command.dtos.MovementCommandDTO
import microservice.dungeon.game.aggregates.command.dtos.SellCommandDTO
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
import microservice.dungeon.game.assertions.CustomAssertions
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import microservice.dungeon.game.contracts.round.web.robot.SendBlockingCommandsToRobotSuccessful
import microservice.dungeon.game.contracts.round.web.robot.SendMovementCommandsToRobotSuccessful
import microservice.dungeon.game.contracts.round.web.robot.resources.RobotCommandInput
import microservice.dungeon.game.contracts.round.web.trading.SendSellingCommandsToTradingSuccessful
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.util.*

class RobotCommandDispatcherClientContractTest {
    private var mockWebServer: MockWebServer? = null
    private var robotCommandDispatcherClient: RobotCommandDispatcherClient? = null


    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer!!.start()
        robotCommandDispatcherClient = RobotCommandDispatcherClient(mockWebServer!!.url("/").toString())
    }


    @Test
    fun shouldConformToSendBlockingCommandsSuccessful() {
        // given
        val contract = SendBlockingCommandsToRobotSuccessful()
        val inputCommands = listOf(
            BlockCommandDTO.fromCommand(
                contract.makeCommandFromContract(buildRobotCommand())
            )
        )
        val mockResponse = MockResponse()
            .setResponseCode(contract.getExpectedResponseCode())
        mockWebServer!!.enqueue(mockResponse)

        // when
        robotCommandDispatcherClient!!.sendBlockingCommands(inputCommands)

        // and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRequestBody = recordedRequest.body.readUtf8()

        // then
        assertThat(contract)
            .conformsWithRequest(recordedRequest)
        assertThat(contract)
            .conformsWithRequestBody(recordedRequestBody)
    }

    @Test
    fun shouldConformToSendMovementCommandsSuccessful() {
        // given
        val contract = SendMovementCommandsToRobotSuccessful()
        val inputCommands = listOf(
            MovementCommandDTO.fromCommand(
                contract.makeCommandFromContract(buildRobotCommand())
            )
        )
        val mockResponse = MockResponse()
            .setResponseCode(contract.getExpectedResponseCode())
        mockWebServer!!.enqueue(mockResponse)

        // when
        robotCommandDispatcherClient!!.sendMovementCommands(inputCommands)

        // and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRequestBody = recordedRequest.body.readUtf8()

        // then
        assertThat(contract)
            .conformsWithRequest(recordedRequest)
        assertThat(contract)
            .conformsWithRequestBody(recordedRequestBody)
    }



    private fun buildRobotCommand(): (RobotCommandInput) -> Command = { input ->
        Command(
            transactionId = input.transactionId,
            gameId = input.gameId,
            playerId = input.playerId,
            robotId = input.robotId,
            commandType = input.commandType,
            roundNumber = input.roundNumber,
            commandObject = CommandObject(
                commandType = input.commandType,
                planetId = input.planetId,
                targetId = input.targetId,
                itemName = input.itemName,
                ItemQuantity = input.itemQuantity
            )
        ) }
}