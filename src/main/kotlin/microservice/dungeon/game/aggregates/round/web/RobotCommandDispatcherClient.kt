package microservice.dungeon.game.aggregates.round.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.round.web.dto.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class RobotCommandDispatcherClient @Autowired constructor(
    @Value(value = "\${rest.robot.baseurl}")
    private val robotBaseURL: String
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val webClient = WebClient.create(robotBaseURL)

    /*fun sendBlockingCommands(commands: List<BlockCommandDto>) {
        if (commands.isEmpty()) {
            logger.debug("No Blocking-Commands to dispatch.")
            return
        }
        return try {
            logger.debug("Starting to dispatch {} Blocking-Commands to RobotService ...", commands.size)
            transmitCommandsToRobot(RobotCommandWrapperDto.makeFromDTOList(commands))
            logger.debug("... dispatching of Blocking-Commands completed.")

        } catch (e: Exception) {
            logger.error("... dispatching of Blocking-Commands failed!")
            logger.error(e.message)
        }
    }
*/
    /*
    fun sendMovementItemUseCommands(commands: List<UseItemMovementCommandDto>) {
        if (commands.isEmpty()) {
            logger.debug("No Movement-ItemUse-Commands to dispatch.")
            return
        }
        return try {
            logger.debug("Starting to dispatch {} Movement-ItemUse-Commands to RobotService ...", commands.size)
            transmitCommandsToRobot(RobotCommandWrapperDto.makeFromDTOList(commands))
            logger.debug("... dispatching of Movement-ItemUse-Commands completed.")

        } catch (e: Exception) {
            logger.error("... dispatching of Movement-ItemUse-Commands failed!")
            logger.error(e.message)
        }
    }
*/
    fun sendMovementCommands(commands: List<MovementCommandDto>) {
        if (commands.isEmpty()) {
            logger.debug("No Movement-Commands to dispatch.")
            return
        }
        return try {
            logger.debug("Starting to dispatch {} Movement-Commands to RobotService ...", commands.size)
            transmitCommandsToRobot(RobotCommandWrapperDto.makeFromDTOList(commands))
            logger.debug("... dispatching of Movement-Commands completed.")

        } catch (e: Exception) {
            logger.error("... dispatching of Movement-Commands failed!")
            logger.error(e.message)
        }
    }
/*
    fun sendBattleItemUseCommands(commands: List<UseItemFightCommandDto>) {
        if (commands.isEmpty()) {
            logger.debug("No Battle-ItemUse-Commands to dispatch.")
            return
        }
        return try {
            logger.debug("Starting to dispatch {} Battle-ItemUse-Commands to RobotService ...", commands.size)
            transmitCommandsToRobot(RobotCommandWrapperDto.makeFromDTOList(commands))
            logger.debug("... dispatching of Battle-ItemUse-Commands completed.")

        } catch (e: Exception) {
            logger.error("... dispatching of Battle-ItemUse-Commands failed!")
            logger.error(e.message)
        }
    }
*/
    fun sendBattleCommands(commands: List<FightCommandDto>) {
        if (commands.isEmpty()) {
            logger.debug("No Battle-Commands to dispatch.")
            return
        }
        return try {
            logger.debug("Starting to dispatch {} Battle-Commands to RobotService ...", commands.size)
            transmitCommandsToRobot(RobotCommandWrapperDto.makeFromDTOList(commands))
            logger.debug("... dispatching of Battle-Commands completed.")

        } catch (e: Exception) {
            logger.error("... dispatching of Battle-Commands failed!")
            logger.error(e.message)
        }
    }

    fun sendMiningCommands(commands: List<MineCommandDto>) {
        if (commands.isEmpty()) {
            logger.debug("No Mining-Commands to dispatch.")
            return
        }
        return try {
            logger.debug("Starting to dispatch {} Mining-Commands to RobotService ...", commands.size)
            transmitCommandsToRobot(RobotCommandWrapperDto.makeFromDTOList(commands))
            logger.debug("... dispatching of Mining-Commands completed.")

        } catch (e: Exception) {
            logger.error("... dispatching of Mining-Commands failed!")
            logger.error(e.message)
        }
    }
/*
    fun sendRepairItemUseCommands(commands: List<UseItemRepairCommandDto>) {
        if (commands.isEmpty()) {
            logger.debug("No Repair-ItemUse-Commands to dispatch.")
            return
        }
        return try {
            logger.debug("Starting to dispatch {} Repair-ItemUse-Commands to RobotService ...", commands.size)
            transmitCommandsToRobot(RobotCommandWrapperDto.makeFromDTOList(commands))
            logger.debug("... dispatching of Repair-ItemUse-Commands completed.")

        } catch (e: Exception) {
            logger.error("... dispatching of Repair-ItemUse-Commands failed!")
            logger.error(e.message)
        }
    }
*/
    fun sendRegeneratingCommands(commands: List<RegenerateCommandDto>) {
        if (commands.isEmpty()) {
            logger.debug("No Regeneration-Commands to dispatch.")
            return
        }
        return try {
            logger.debug("Starting to dispatch {} Regeneration-Commands to RobotService ...", commands.size)
            transmitCommandsToRobot(RobotCommandWrapperDto.makeFromDTOList(commands))
            logger.debug("... dispatching of Regeneration-Commands completed.")

        } catch (e: Exception) {
            logger.error("... dispatching of Regeneration-Commands failed!")
            logger.error(e.message)
        }
    }

    private fun transmitCommandsToRobot(wrappedCommands: RobotCommandWrapperDto) {
        logger.trace("Robot-Endpoint is: POST ${robotBaseURL}/commands")
        logger.trace(ObjectMapper().writeValueAsString(wrappedCommands))

        webClient.post().uri("/commands")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(wrappedCommands)
            .exchangeToMono{ clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.ACCEPTED) {
                    clientResponse.bodyToMono(String::class.java)
                }
                else {
                    throw Exception("Connection failed w/ status-code: ${clientResponse.statusCode()}")
                }
            }.block()
    }
}