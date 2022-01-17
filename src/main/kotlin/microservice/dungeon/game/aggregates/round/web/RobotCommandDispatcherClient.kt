package microservice.dungeon.game.aggregates.round.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.command.dtos.*
import microservice.dungeon.game.aggregates.round.web.dto.*
import mu.KotlinLogging
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
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val webClient = WebClient.create(robotBaseURL)

    fun sendBlockingCommands(commands: List<BlockCommandDTO>) {
        logger.debug("Starting to dispatch Blocking-Commands to RobotService ... [commandSize=${commands.size}]")

        return try {
            transmitCommandsToRobot(RobotCommandWrapperDTO.makeFromDTOList(commands))
            logger.debug("... dispatching of Blocking-Commands successful.")

        } catch (e: Exception) {
            logger.error("... dispatching of Blocking-Commands failed!")
            logger.error(e.message)
        }
    }

    fun sendMovementItemUseCommands(commands: List<UseItemMovementCommandDTO>) {
        logger.debug("Starting to dispatch Movement-ItemUse-Commands to RobotService ... [commandSize=${commands.size}]")

        return try {
            transmitCommandsToRobot(RobotCommandWrapperDTO.makeFromDTOList(commands))
            logger.debug("... dispatching of Movement-ItemUse-Commands successful.")

        } catch (e: Exception) {
            logger.error("... dispatching of Movement-ItemUse-Commands failed!")
            logger.error(e.message)
        }
    }

    fun sendMovementCommands(commands: List<MovementCommandDTO>) {
        logger.debug("Starting to dispatch Movement-Commands to RobotService ... [commandSize=${commands.size}]")

        return try {
            transmitCommandsToRobot(RobotCommandWrapperDTO.makeFromDTOList(commands))
            logger.debug("... dispatching of Movement-Commands successful.")

        } catch (e: Exception) {
            logger.error("... dispatching of Movement-Commands failed!")
            logger.error(e.message)
        }
    }

    fun sendBattleItemUseCommands(commands: List<UseItemFightCommandDTO>) {
        logger.debug("Starting to dispatch Battle-ItemUse-Commands to RobotService ... [commandSize=${commands.size}]")

        return try {
            transmitCommandsToRobot(RobotCommandWrapperDTO.makeFromDTOList(commands))
            logger.debug("... dispatching of Battle-ItemUse-Commands successful.")

        } catch (e: Exception) {
            logger.error("... dispatching of Battle-ItemUse-Commands failed!")
            logger.error(e.message)
        }
    }

    fun sendBattleCommands(commands: List<FightCommandDTO>) {
        logger.debug("Starting to dispatch Battle-Commands to RobotService ... [commandSize=${commands.size}]")

        return try {
            transmitCommandsToRobot(RobotCommandWrapperDTO.makeFromDTOList(commands))
            logger.debug("... dispatching of Battle-Commands successful.")

        } catch (e: Exception) {
            logger.error("... dispatching of Battle-Commands failed!")
            logger.error(e.message)
        }
    }

    fun sendMiningCommands(commands: List<MineCommandDTO>) {
        logger.debug("Starting to dispatch Mining-Commands to RobotService ... [commandSize=${commands.size}]")

        return try {
            transmitCommandsToRobot(RobotCommandWrapperDTO.makeFromDTOList(commands))
            logger.debug("... dispatching of Mining-Commands successful.")

        } catch (e: Exception) {
            logger.error("... dispatching of Mining-Commands failed!")
            logger.error(e.message)
        }
    }

    fun sendRepairItemUseCommands(commands: List<UseItemRepairCommandDTO>) {
        logger.debug("Starting to dispatch Repair-ItemUse-Commands to RobotService ... [commandSize=${commands.size}]")

        return try {
            transmitCommandsToRobot(RobotCommandWrapperDTO.makeFromDTOList(commands))
            logger.debug("... dispatching of Repair-ItemUse-Commands successful.")

        } catch (e: Exception) {
            logger.error("... dispatching of Repair-ItemUse-Commands failed!")
            logger.error(e.message)
        }
    }

    fun sendRegeneratingCommands(commands: List<RegenerateCommandDTO>) {
        logger.debug("Starting to dispatch Regeneration-Commands to RobotService ... [commandSize=${commands.size}]")

        return try {
            transmitCommandsToRobot(RobotCommandWrapperDTO.makeFromDTOList(commands))
            logger.debug("... dispatching of Regeneration-Commands successful.")

        } catch (e: Exception) {
            logger.error("... dispatching of Regeneration-Commands failed!")
            logger.error(e.message)
        }
    }

    private fun transmitCommandsToRobot(wrappedCommands: RobotCommandWrapperDTO) {
        logger.trace("Endpoint is: POST ${robotBaseURL}/commands")

        webClient.post().uri("/commands")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(ObjectMapper().writeValueAsString(wrappedCommands))
            .exchangeToMono{ clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.ACCEPTED) {
                    clientResponse.bodyToMono(JsonNode::class.java)
                }
                else {
                    throw Exception("Connection failed w/ status-code: ${clientResponse.statusCode()}")
                }
            }.block()
    }
}