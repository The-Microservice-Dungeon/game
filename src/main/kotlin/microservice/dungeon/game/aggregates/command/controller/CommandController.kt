package microservice.dungeon.game.aggregates.command.controller

import microservice.dungeon.game.aggregates.command.controller.dto.CommandRequestDto
import microservice.dungeon.game.aggregates.command.controller.dto.CommandResponseDto
import microservice.dungeon.game.aggregates.command.controller.dto.RoundCommandsResponseDto
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandArgumentException
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.command.services.CommandService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameNotFoundException
import microservice.dungeon.game.aggregates.game.domain.GameStateException
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.player.domain.PlayerNotFoundException
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class CommandController @Autowired constructor(
    private val commandService: CommandService,
    private val commandRepository: CommandRepository,
    private val roundRepository: RoundRepository,
    private val gameRepository: GameRepository
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostMapping("/commands", consumes = ["application/json"], produces = ["application/json"])
    fun createNewCommand(@RequestBody requestBody: CommandRequestDto): ResponseEntity<CommandResponseDto> {
        logger.debug("Request to create new command received ...")
        logger.trace("POST: /commands")

        val commandType: CommandType
        try {
            commandType = CommandType.getTypeFromString(requestBody.commandType)
        } catch (e: Exception) {
            logger.warn("Request to create new command failed. Provided commandType invalid. [input=${requestBody.commandType}]")
            logger.warn(e.message)
            logger.trace("Responding with 400.")
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        return try {
            val transactionId = commandService.createNewCommand(
                requestBody.gameId, requestBody.playerToken, requestBody.robotId, commandType, requestBody
            )
            val responseBody = CommandResponseDto(transactionId)

            logger.debug("Request to create new command was successful.")
            logger.trace("Responding with 201.")
            ResponseEntity(responseBody, HttpStatus.CREATED)

        } catch (e: PlayerNotFoundException) {
            logger.warn("Request to create new command failed. Player not found.")
            logger.warn(e.message)
            logger.trace("Responding with 404.")
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: GameNotFoundException) {
            logger.warn("Request to create new command failed. Game not found.")
            logger.warn(e.message)
            logger.trace("Responding with 404.")
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: GameStateException) {
            logger.warn("Request to create new command failed. Game not ready.")
            logger.warn(e.message)
            logger.trace("Responding with 403.")
            ResponseEntity(HttpStatus.FORBIDDEN)

        } catch (e: CommandArgumentException) {
            logger.warn("Request to create new command failed. Command-Inputs invalid.")
            logger.warn(e.message)
            logger.trace("Responding with 403.")
            ResponseEntity(HttpStatus.FORBIDDEN)

        } catch (e: Exception) {
            logger.warn("Request to create new command failed for unknown reasons.")
            logger.warn(e.message)
            logger.trace("Responding with 500")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/commands", produces = ["application/json"])
    fun getAllRoundCommands(@RequestParam(name = "gameId") gameId: UUID, @RequestParam(name = "roundNumber") roundNumber: Int): ResponseEntity<RoundCommandsResponseDto> {
        logger.debug("Request to collect all round-commands received ... [gameId=$gameId, roundNumber=$roundNumber]")

        val game: Game
        val round: Round

        // TODO BAD WORKAROUND
        try {
            game = gameRepository.findById(gameId).get()
            round = roundRepository.findRoundByGameAndRoundNumber(game, roundNumber).get()

        } catch (e: Exception) {
            logger.warn("Failed to process request. Game or round were not found.")
            logger.trace("Responding with 404.")
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        return try {
            val commands: List<Command> = commandRepository.findAllCommandsByRound(round)
            val responseDto = RoundCommandsResponseDto(round, commands)

            logger.debug("Request successful. Returning ${commands.size} commands.")
            logger.trace("Responding with 200.")
            ResponseEntity(responseDto, HttpStatus.OK)

        } catch (e: Exception) {
            logger.error("Failed to process request for internal reasons.")
            logger.error(e.message)
            logger.trace("Responding with 500.")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

//    @GetMapping("/commands", produces = ["application/json"])
//    fun getAllRoundCommands(@RequestParam gameId: UUID, @RequestParam roundNumber: Int): ResponseEntity<List<Command>> {
////        try {
////            val roundCommands = commandService.getAllRoundCommands(gameId, roundNumber)
////            if (roundCommands != null) {
////                return ResponseEntity(roundCommands, HttpStatus.OK)
////            } else {
////                throw ResponseStatusException(HttpStatus.NOT_FOUND, "roundNumber not found")
////            }
////        } catch (e: IllegalArgumentException) {
////            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString())
////        } catch (e: Exception) {
////            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString())
////        }
//        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
//    }
}