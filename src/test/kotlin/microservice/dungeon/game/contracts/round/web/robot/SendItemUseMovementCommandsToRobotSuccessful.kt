package microservice.dungeon.game.contracts.round.web.robot

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.contracts.RestProducerContract
import microservice.dungeon.game.contracts.round.web.robot.resources.RobotCommandInput
import org.springframework.http.MediaType
import java.util.*
/*
class SendItemUseMovementCommandsToRobotSuccessful: RestProducerContract {

    // for
    private val requestVerb = "POST"
    private val requestPath = "/commands"
    private val requestHttpHeaderContentType = MediaType.APPLICATION_JSON_VALUE


    // with
    private val commandInput = RobotCommandInput(
        transactionId = UUID.randomUUID(),
        robotId = UUID.randomUUID(),
        itemName = "ANY_NAME",
   //     commandType = CommandType.MOVEITEMUSE
    )
    fun makeCommandFromContract(builder: (RobotCommandInput) -> Command): Command = builder(commandInput)


    // expect
    private val expectedResponseCode = 202
    private val expectedResponseBody =
        """
            |{
                |"commands":[
                    |"use-item-movement ${commandInput.robotId} ${commandInput.itemName} ${commandInput.transactionId}"
                |]
            |}
        """
            .trimMargin()
            .replace("\n", "")

    override fun getRequestVerb() = requestVerb

    override fun getRequestPath() = requestPath

    override fun getRequestHttpHeaderContentType() = requestHttpHeaderContentType

    override fun getExpectedResponseCode() = expectedResponseCode

    override fun getExpectedResponseBody() = expectedResponseBody
}
*/