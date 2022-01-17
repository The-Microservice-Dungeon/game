package microservice.dungeon.game.integrationtests.model.round.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandObject
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.round.web.dto.BuyCommandDTO
import microservice.dungeon.game.aggregates.round.web.dto.SellCommandDTO
import microservice.dungeon.game.aggregates.round.web.TradingCommandDispatcherClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.util.*

class TradingCommandDispatcherClientIntegrationTest {
    private var mockWebServer: MockWebServer? = null
    private var tradingCommandDispatcherClient: TradingCommandDispatcherClient? = null

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())


    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer!!.start()
        tradingCommandDispatcherClient = TradingCommandDispatcherClient(mockWebServer!!.url("/").toString())
    }

    @Test
    fun shouldAllowToSendSellingCommands() {
        // given
        val inputCommands = listOf(
            SellCommandDTO.fromCommand(makeAnyTradingCommand(CommandType.SELLING)),
            SellCommandDTO.fromCommand(makeAnyTradingCommand(CommandType.SELLING))
        )
        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer!!.enqueue(mockResponse)

        // when
        tradingCommandDispatcherClient!!.sendSellingCommands(inputCommands)

        // and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedSellingCommandDTOs: List<SellCommandDTO> = objectMapper.readValue(
            recordedRequest.body.readUtf8()
        )

        // then
        assertThat(recordedRequest.method)
            .isEqualTo("POST")
        assertThat(recordedRequest.path)
            .isEqualTo("/commands")
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        // and
        assertThat(recordedSellingCommandDTOs)
            .isEqualTo(inputCommands)
    }

    @Test
    fun shouldAllowToSendBuyingCommands() {
        // given
        val inputCommands = listOf(
            BuyCommandDTO.fromCommand(makeAnyTradingCommand(CommandType.BUYING)),
            BuyCommandDTO.fromCommand(makeAnyTradingCommand(CommandType.BUYING))
        )
        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer!!.enqueue(mockResponse)

        // when
        tradingCommandDispatcherClient!!.sendBuyingCommands(inputCommands)

        // and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedBuyingCommandDTOs: List<BuyCommandDTO> = objectMapper.readValue(
            recordedRequest.body.readUtf8()
        )

        // then
        assertThat(recordedRequest.method)
            .isEqualTo("POST")
        assertThat(recordedRequest.path)
            .isEqualTo("/commands")
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        // and
        assertThat(recordedBuyingCommandDTOs)
            .isEqualTo(inputCommands)
    }

    private fun makeAnyTradingCommand(commandType: CommandType) =
        Command(
            gameId = UUID.randomUUID(),
            playerId = UUID.randomUUID(),
            robotId = UUID.randomUUID(),
            commandType = commandType,
            commandObject = CommandObject(
                commandType = commandType,
                planetId = UUID.randomUUID(),
                targetId = UUID.randomUUID(),
                itemName = "ANY ITEM_NAME",
                itemQuantity = 3
            ),
            roundNumber = 5
        )
}