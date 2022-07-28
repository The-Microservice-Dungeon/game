package microservice.dungeon.game.contracttests.model.round.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandPayload
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.web.dto.BuyCommandDto
import microservice.dungeon.game.aggregates.round.web.dto.SellCommandDto
import microservice.dungeon.game.aggregates.round.web.TradingCommandDispatcherClient
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import microservice.dungeon.game.contracts.round.web.trading.SendBuyingCommandsToTradingSuccessful
import microservice.dungeon.game.contracts.round.web.trading.SendSellingCommandsToTradingSuccessful
import microservice.dungeon.game.contracts.round.web.trading.resources.TradingCommandInput
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.*
import org.junit.jupiter.api.Disabled

@Disabled
class TradingCommandDispatcherClientContractTest {
    private var mockWebServer: MockWebServer? = null
    private var tradingCommandDispatcherClient: TradingCommandDispatcherClient? = null

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    private val ANY_ROUND_NUMBER = 3
    private val ANY_ROBOT_ID = UUID.randomUUID()
    private val ANY_TARGET_ID = UUID.randomUUID()


    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer!!.start()
        tradingCommandDispatcherClient = TradingCommandDispatcherClient(mockWebServer!!.url("/").toString())
    }

    @Test
    fun shouldConformToSendSellingCommandsSuccessful() {
        // given
        val contract = SendSellingCommandsToTradingSuccessful()
        val inputCommands = listOf(
            SellCommandDto.makeFromCommand(
                contract.makeCommandFromContract(builtTradingCommand())
            )
        )
        val mockResponse = MockResponse()
            .setResponseCode(contract.getExpectedResponseCode())
        mockWebServer!!.enqueue(mockResponse)

        // when
        tradingCommandDispatcherClient!!.sendSellingCommands(inputCommands)

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
    fun shouldConformToSendBuyingCommandsSuccessful() {
        // given
        val contract = SendBuyingCommandsToTradingSuccessful()
        val inputCommands = listOf(
            BuyCommandDto.makeFromCommand(
                contract.makeCommandFromContract(builtTradingCommand())
            )
        )
        val mockResponse = MockResponse()
            .setResponseCode(contract.getExpectedResponseCode())
        mockWebServer!!.enqueue(mockResponse)

        // when
        tradingCommandDispatcherClient!!.sendBuyingCommands(inputCommands)

        // and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRequestBody = recordedRequest.body.readUtf8()

        // then
        assertThat(contract)
            .conformsWithRequest(recordedRequest)
        assertThat(contract)
            .conformsWithRequestBody(recordedRequestBody)
    }

    private fun builtTradingCommand(): (TradingCommandInput) -> Command = { input ->
        Command(
            commandId = input.transactionId,
            round = mock<Round>().also { mock: Round ->
                whenever(mock.getRoundNumber()).thenReturn(ANY_ROUND_NUMBER)
                whenever(mock.getGameId()).thenReturn(input.gameId)
            },
            player = mock<Player>().also { mock: Player ->
                whenever(mock.getPlayerId()).thenReturn(input.playerId)
            },
            robot = mock<Robot>().also { mock: Robot ->
                whenever(mock.getRobotId()).thenReturn(ANY_ROBOT_ID)
            },
            commandType = input.commandType,
            commandPayload = CommandPayload(
                planetId = input.planetId,
                targetId = ANY_TARGET_ID,
                itemName = input.itemName,
                itemQuantity = input.amount
            )
        )
    }
}