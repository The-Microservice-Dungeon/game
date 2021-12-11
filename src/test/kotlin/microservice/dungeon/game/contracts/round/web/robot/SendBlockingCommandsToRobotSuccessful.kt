package microservice.dungeon.game.contracts.round.web.robot

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.contracts.round.web.robot.resources.RobotCommandInput
import microservice.dungeon.game.contracts.round.web.trading.resources.TradingCommandInput
import java.util.*

class SendBlockingCommandsToRobotSuccessful {

    // for
    val requestVerb = "POST"
    val requestPath = "/commands"


    // with
    private val commandInput = RobotCommandInput(
        transactionId = UUID.randomUUID(),
        robotId = UUID.randomUUID(),
        commandType = CommandType.BLOCKING
    )
    fun makeCommandFromContract(builder: (RobotCommandInput) -> Command): Command = builder(commandInput)


    // expect
    val expectedResponseCode = 202
    val expectedResponseBody =
        """
            |{
                |"commands":[
                    |"block ${commandInput.robotId} ${commandInput.transactionId}"
                |]
            |}
        """
            .trimMargin()
            .replace("\n", "")
}