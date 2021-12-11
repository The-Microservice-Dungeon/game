package microservice.dungeon.game.contracttests.model.round.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandObject
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.dtos.BuyCommandDTO
import microservice.dungeon.game.aggregates.command.dtos.SellCommandDTO
import microservice.dungeon.game.aggregates.round.web.TradingCommandDispatcherClient
import microservice.dungeon.game.contracts.round.web.trading.SendBuyingCommandsToTradingSuccessful
import microservice.dungeon.game.contracts.round.web.trading.SendSellingCommandsToTradingSuccessful
import microservice.dungeon.game.contracts.round.web.trading.resources.TradingCommandInput
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
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
    fun shouldSendSellingCommandsSuccessful() {
        // given
        val contract = SendSellingCommandsToTradingSuccessful()
        val inputCommands = listOf(
            SellCommandDTO.fromCommand(
                contract.makeCommandFromContract(builtTradingCommand())
            )
        )
        val mockResponse = MockResponse()
            .setResponseCode(contract.expectedResponseCode)
        mockWebServer!!.enqueue(mockResponse)

        // when
        tradingCommandDispatcherClient!!.sendSellingCommands(inputCommands)

        // and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRequestBody = recordedRequest.body.readUtf8()

        // then
        assertThat(recordedRequest.method)
            .isEqualTo(contract.requestVerb)
        assertThat(recordedRequest.path)
            .isEqualTo(contract.requestPath)
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        // and
        assertThat(recordedRequestBody)
            .isEqualTo(contract.expectedResponseBody)
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
                ItemQuantity = input.amount
            )
        ) }
}