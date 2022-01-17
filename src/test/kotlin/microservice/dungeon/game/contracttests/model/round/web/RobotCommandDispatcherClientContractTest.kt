package microservice.dungeon.game.contracttests.model.round.web

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandPayload
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
import microservice.dungeon.game.aggregates.round.web.dto.*
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import microservice.dungeon.game.contracts.round.web.robot.*
import microservice.dungeon.game.contracts.round.web.robot.resources.RobotCommandInput
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

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
            BlockCommandDto.makeFromCommand(
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
            MovementCommandDto.makeFromCommand(
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

    @Test
    fun shouldConformToSendBattleCommandsSuccessful() {
        // given
        val contract = SendBattleCommandsToRobotSuccessful()
        val inputCommands = listOf(
            FightCommandDto.makeFromCommand(
                contract.makeCommandFromContract(buildRobotCommand())
            )
        )
        val mockResponse = MockResponse()
            .setResponseCode(contract.getExpectedResponseCode())
        mockWebServer!!.enqueue(mockResponse)

        // when
        robotCommandDispatcherClient!!.sendBattleCommands(inputCommands)

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
    fun shouldConformToSendMiningCommandsSuccessful() {
        // given
        val contract = SendMiningCommandsToRobotSuccessful()
        val inputCommands = listOf(
            MineCommandDto.makeFromCommand(
                contract.makeCommandFromContract(buildRobotCommand())
            )
        )
        val mockResponse = MockResponse()
            .setResponseCode(contract.getExpectedResponseCode())
        mockWebServer!!.enqueue(mockResponse)

        // when
        robotCommandDispatcherClient!!.sendMiningCommands(inputCommands)

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
    fun shouldConformToSendRegeneratingCommandsSuccessful() {
        // given
        val contract = SendRegeneratingCommandsToRobotSuccessful()
        val inputCommands = listOf(
            RegenerateCommandDto.makeFromCommands(
                contract.makeCommandFromContract(buildRobotCommand())
            )
        )
        val mockResponse = MockResponse()
            .setResponseCode(contract.getExpectedResponseCode())
        mockWebServer!!.enqueue(mockResponse)

        // when
        robotCommandDispatcherClient!!.sendRegeneratingCommands(inputCommands)

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
    fun shouldConformToSendItemUseBattleCommandsSuccessful() {
        // given
        val contract = SendItemUseBattleCommandsToRobotSuccessful()
        val inputCommands = listOf(
            UseItemFightCommandDto.makeFromCommand(
                contract.makeCommandFromContract(buildRobotCommand())
            )
        )
        val mockResponse = MockResponse()
            .setResponseCode(contract.getExpectedResponseCode())
        mockWebServer!!.enqueue(mockResponse)

        // when
        robotCommandDispatcherClient!!.sendBattleItemUseCommands(inputCommands)

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
    fun shouldConformToSendItemUseRepairCommandsSuccessful() {
        // given
        val contract = SendItemUseRepairCommandsToRobotSuccessful()
        val inputCommands = listOf(
            UseItemRepairCommandDto.makeFromCommand(
                contract.makeCommandFromContract(buildRobotCommand())
            )
        )
        val mockResponse = MockResponse()
            .setResponseCode(contract.getExpectedResponseCode())
        mockWebServer!!.enqueue(mockResponse)

        // when
        robotCommandDispatcherClient!!.sendRepairItemUseCommands(inputCommands)

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
    fun shouldConformToSendItemUseMovementCommandsSuccessful() {
        // given
        val contract = SendItemUseMovementCommandsToRobotSuccessful()
        val inputCommands = listOf(
            UseItemMovementCommandDto.makeFromCommand(
                contract.makeCommandFromContract(buildRobotCommand())
            )
        )
        val mockResponse = MockResponse()
            .setResponseCode(contract.getExpectedResponseCode())
        mockWebServer!!.enqueue(mockResponse)

        // when
        robotCommandDispatcherClient!!.sendMovementItemUseCommands(inputCommands)

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
            commandId = input.transactionId,
            round = mock<Round>().also { mock: Round ->
                whenever(mock.getRoundNumber()).thenReturn(input.roundNumber)
                whenever(mock.getGameId()).thenReturn(input.gameId)
            },
            player = mock<Player>().also { mock: Player ->
                whenever(mock.getPlayerId()).thenReturn(input.playerId)
            },
            robot = mock<Robot>().also { mock: Robot ->
                whenever(mock.getRobotId()).thenReturn(input.robotId)
            },
            commandType = input.commandType,
            commandPayload = CommandPayload(
                planetId = input.planetId,
                targetId = input.targetId,
                itemName = input.itemName,
                itemQuantity = input.itemQuantity
            )
        )
    }
}