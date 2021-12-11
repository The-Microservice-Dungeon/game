package microservice.dungeon.game.contracts.round.web.trading

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.contracts.round.web.trading.resources.TradingCommandInput
import java.util.*

class SendSellingCommandsToTradingSuccessful {

    // for
    val requestVerb = "POST"
    val requestPath = "/commands"


    // with
    private val commandInput = TradingCommandInput(
        transactionId = UUID.randomUUID(),
        gameId = UUID.randomUUID(),
        playerId = UUID.randomUUID(),
        commandType = CommandType.SELLING,
        amount = 5,
        planetId = UUID.randomUUID(),
        itemName = "ROBOT"
    )
    fun makeCommandFromContract(builder: (TradingCommandInput) -> Command): Command = builder(commandInput)


    // expect
    val expectedResponseCode = 200
    val expectedResponseBody =
        """
        |[{
            |"transactionId":"${commandInput.transactionId}",
            |"playerId":"${commandInput.playerId}",
            |"payload":{
                |"commandType":"sell",
                |"amount":${commandInput.amount},
                |"planetId":"${commandInput.planetId}",
                |"itemName":"${commandInput.itemName}"
            |}
        |}]
        """
        .trimMargin()
        .replace("\n", "")
}