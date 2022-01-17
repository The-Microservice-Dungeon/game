package microservice.dungeon.game.integrationtests.model.round.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandPayload
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.web.dto.BuyCommandDto
import microservice.dungeon.game.aggregates.round.web.dto.SellCommandDto
import microservice.dungeon.game.aggregates.round.web.TradingCommandDispatcherClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
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
            SellCommandDto.makeFromCommand(makeAnyTradingCommand(CommandType.SELLING)),
            SellCommandDto.makeFromCommand(makeAnyTradingCommand(CommandType.SELLING))
        )
        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer!!.enqueue(mockResponse)

        // when
        tradingCommandDispatcherClient!!.sendSellingCommands(inputCommands)

        // and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedSellingCommandDTOs: List<SellCommandDto> = objectMapper.readValue(
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
            BuyCommandDto.makeFromCommand(makeAnyTradingCommand(CommandType.BUYING)),
            BuyCommandDto.makeFromCommand(makeAnyTradingCommand(CommandType.BUYING))
        )
        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer!!.enqueue(mockResponse)

        // when
        tradingCommandDispatcherClient!!.sendBuyingCommands(inputCommands)

        // and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedBuyingCommandDTOs: List<BuyCommandDto> = objectMapper.readValue(
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
            commandId = UUID.randomUUID(),
            round = mock<Round>().also { mock: Round ->
                whenever(mock.getRoundNumber()).thenReturn(4)
                whenever(mock.getGameId()).thenReturn(UUID.randomUUID())
            },
            player = mock<Player>().also { mock: Player ->
                whenever(mock.getPlayerId()).thenReturn(UUID.randomUUID())
            },
            robot = mock<Robot>().also { mock: Robot ->
                whenever(mock.getRobotId()).thenReturn(null)
            },
            commandType = commandType,
            commandPayload = CommandPayload(
                planetId = UUID.randomUUID(),
                targetId = UUID.randomUUID(),
                itemName = "ROBOT",
                itemQuantity = 1
            )
        )

}