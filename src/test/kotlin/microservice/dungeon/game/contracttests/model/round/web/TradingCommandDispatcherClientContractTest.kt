package microservice.dungeon.game.contracttests.model.round.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandObject
import microservice.dungeon.game.aggregates.round.web.dto.BuyCommandDTO
import microservice.dungeon.game.aggregates.round.web.dto.SellCommandDTO
import microservice.dungeon.game.aggregates.round.web.TradingCommandDispatcherClient
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import microservice.dungeon.game.contracts.round.web.trading.SendBuyingCommandsToTradingSuccessful
import microservice.dungeon.game.contracts.round.web.trading.SendSellingCommandsToTradingSuccessful
import microservice.dungeon.game.contracts.round.web.trading.resources.TradingCommandInput
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

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
            SellCommandDTO.fromCommand(
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
            BuyCommandDTO.fromCommand(
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
            transactionId = input.transactionId,
            gameId = input.gameId,
            playerId = input.playerId,
            robotId = ANY_ROBOT_ID,
            commandType = input.commandType,
            roundNumber = ANY_ROUND_NUMBER,
            commandObject = CommandObject(
                commandType = input.commandType,
                planetId = input.planetId,
                targetId = ANY_TARGET_ID,
                itemName = input.itemName,
                itemQuantity = input.amount
            )
        ) }
}