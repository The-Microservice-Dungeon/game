package microservice.dungeon.game.contracts.round.web.robot

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.contracts.RestProducerContract
import microservice.dungeon.game.contracts.round.web.robot.resources.RobotCommandInput
import org.springframework.http.MediaType
import java.util.*

class SendMiningCommandsToRobotSuccessful: RestProducerContract {

    // for
    private val requestVerb = "POST"
    private val requestPath = "/commands"
    private val requestHttpHeaderContentType = MediaType.APPLICATION_JSON_VALUE


    // with
    private val commandInput = RobotCommandInput(
        transactionId = UUID.randomUUID(),
        robotId = UUID.randomUUID(),
        commandType = CommandType.MINING
    )
    fun makeCommandFromContract(builder: (RobotCommandInput) -> Command): Command = builder(commandInput)


    // expect
    private val expectedResponseCode = 202
    private val expectedResponseBody =
        """
            |{
                |"commands":[
                    |"mine ${commandInput.robotId} ${commandInput.transactionId}"
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