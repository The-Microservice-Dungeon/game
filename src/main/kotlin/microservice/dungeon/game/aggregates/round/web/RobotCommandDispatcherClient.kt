package microservice.dungeon.game.aggregates.round.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.command.dtos.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class RobotCommandDispatcherClient @Autowired constructor(
    @Value(value = "\${rest.robot.baseurl}")
    private val robotBaseURL: String
) {
    private val webClient = WebClient.create(robotBaseURL)


    fun sendBlockingCommands(commands: List<BlockCommandDTO>) {
        transmitCommandsToRobot(
            RobotCommandWrapperDTO.makeFromDTOList(commands)
        )
    }

    fun sendMovementItemUseCommands(commands: List<UseItemMovementCommandDTO>) {
        transmitCommandsToRobot(
            RobotCommandWrapperDTO.makeFromDTOList(commands)
        )
    }

    fun sendMovementCommands(commands: List<MovementCommandDTO>) {
        transmitCommandsToRobot(
            RobotCommandWrapperDTO.makeFromDTOList(commands)
        )
    }

    fun sendBattleItemUseCommands(commands: List<UseItemFightCommandDTO>) {
        transmitCommandsToRobot(
            RobotCommandWrapperDTO.makeFromDTOList(commands)
        )
    }

    fun sendBattleCommands(commands: List<FightCommandDTO>) {
        transmitCommandsToRobot(
            RobotCommandWrapperDTO.makeFromDTOList(commands)
        )
    }

    private fun transmitCommandsToRobot(wrappedCommands: RobotCommandWrapperDTO) {
        webClient.post().uri("/commands")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(ObjectMapper().writeValueAsString(wrappedCommands))
            .exchangeToMono{ clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.ACCEPTED) {
                    clientResponse.bodyToMono(JsonNode::class.java)
                }
                else {
                    throw Exception("Err")
                }
            }.block()
    }
}