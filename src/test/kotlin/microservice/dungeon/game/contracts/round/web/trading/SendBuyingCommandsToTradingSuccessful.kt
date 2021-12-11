package microservice.dungeon.game.contracts.round.web.trading

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.contracts.RestProducerContract
import microservice.dungeon.game.contracts.round.web.trading.resources.TradingCommandInput
import org.springframework.http.MediaType
import java.util.*

class SendBuyingCommandsToTradingSuccessful: RestProducerContract {

    // for
    private val requestVerb = "POST"
    private val requestPath = "/commands"
    private val requestHttpHeaderContentType = MediaType.APPLICATION_JSON_VALUE


    // with
    private val commandInput = TradingCommandInput(
        transactionId = UUID.randomUUID(),
        gameId = UUID.randomUUID(),
        playerId = UUID.randomUUID(),
        commandType = CommandType.BUYING,
        amount = 5,
        planetId = UUID.randomUUID(),
        itemName = "ROBOT"
    )
    fun makeCommandFromContract(builder: (TradingCommandInput) -> Command): Command = builder(commandInput)


    // expect
    private val expectedResponseCode = 200
    private val expectedResponseBody =
        """
        |[{
            |"transactionId":"${commandInput.transactionId}",
            |"playerId":"${commandInput.playerId}",
            |"payload":{
                |"commandType":"buy",
                |"amount":${commandInput.amount},
                |"planetId":"${commandInput.planetId}",
                |"itemName":"${commandInput.itemName}"
            |}
        |}]
        """
        .trimMargin()
        .replace("\n", "")

    override fun getRequestVerb() = requestVerb

    override fun getRequestPath() = requestPath

    override fun getRequestHttpHeaderContentType() = requestHttpHeaderContentType

    override fun getExpectedResponseCode() = expectedResponseCode

    override fun getExpectedResponseBody() = expectedResponseBody
}