package microservice.dungeon.game.aggregates.game.controller

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.core.EntityNotFoundException
import microservice.dungeon.game.aggregates.core.GameAlreadyFullException
import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.game.controller.dto.CreateGameRequestDto
import microservice.dungeon.game.aggregates.game.controller.dto.CreateGameResponseDto
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.dtos.GameResponseDto
import microservice.dungeon.game.aggregates.game.dtos.GameTimeDto
import microservice.dungeon.game.aggregates.game.dtos.PlayerJoinGameDto
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.servives.GameService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.validation.Valid


@RestController
class GameController @Autowired constructor(
    private val gameService: GameService,
    private val gameRepository: GameRepository
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostMapping("/games", consumes = ["application/json"], produces = ["application/json"])
    fun createNewGame(@RequestBody requestGame: CreateGameRequestDto): ResponseEntity<CreateGameResponseDto> {
        logger.debug("Request to create new game received ...")
        logger.trace("Serialized RequestBody is:")
        logger.trace(requestGame.serialize())

        return try {
            val response: Pair<UUID, Game> = gameService
                .createNewGame(requestGame.maxPlayers, requestGame.maxRounds)
            val responseDto = CreateGameResponseDto(response.second.getGameId())

            logger.debug("Request to create new game was successful. [gameId=${response.second.getGameId()}]")
            logger.trace("Responding with 201. Serialized ResponseBody is:")
            logger.trace(responseDto.serialize())
            ResponseEntity(responseDto, HttpStatus.CREATED)

        } catch (e: Exception) {
            logger.warn("Request to create new game failed.")
            logger.warn(e.message)
            logger.trace("Responding with 403.")
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @PostMapping("/games/{gameId}/gameCommands/start")
    fun startGame(@PathVariable(name = "gameId") gameId: UUID): ResponseEntity<HttpStatus> {
        logger.debug("Request to start game received ... [gameId=$gameId]")

        return try {
            val transactionId: UUID = gameService.startGame(gameId)

            logger.debug("Request to start game was successful. [gameId=$gameId]")
            logger.trace("Responding with 201.")
            ResponseEntity(HttpStatus.CREATED)

        } catch (e: Exception) {
            logger.warn("Request to start game failed. [gameId=$gameId]")
            logger.warn(e.message)
            logger.trace("Responding with 403")
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

//    @GetMapping("/games")
//    fun getAllGames(): MutableIterable<Game> = gameService.getAllGames()
//
//    @GetMapping("/games/{gameId}/time")
//    fun getTime(@PathVariable(value = "gameId") gameId: UUID, @ModelAttribute game: Game): GameTimeDto {
//        return gameService.getGameTime(gameId)
//    }
//
//
//    @PutMapping("/games/{gameId}/players/{playerToken}")
//    fun insertPlayerById (
//        @PathVariable(value = "gameId") gameId: UUID,
//        @PathVariable(value = "playerToken") playerToken: UUID
//    ): ResponseEntity<PlayerJoinGameDto> {
//        try {
//            return gameService.insertPlayer(gameId, playerToken)
//
//        } catch (e: EntityNotFoundException) {
//            throw ResponseStatusException(
//                HttpStatus.BAD_REQUEST,
//                "Player not found"
//            )
//        } catch (e: MethodNotAllowedForStatusException) {
//            throw ResponseStatusException(
//                HttpStatus.BAD_REQUEST,
//                "Wrong game status"
//            )
//        } catch (e: GameAlreadyFullException) {
//            throw ResponseStatusException(
//                HttpStatus.BAD_REQUEST,
//                "Game already full"
//            )
//        } catch (e: EntityAlreadyExistsException) {
//            throw ResponseStatusException(
//                HttpStatus.NOT_ACCEPTABLE,
//                "Player already in Game"
//            )
//        }
//    }
//
//    @PostMapping("/games", consumes = ["application/json"], produces = ["application/json"])
//    fun createNewGame(@RequestBody game: Game): ResponseEntity<GameResponseDto> {
//        try {
//            val newGame = gameService.createNewGame(game)
//
//            val responseGame = GameResponseDto(
//                newGame.getGameId(),
//                newGame.getGameStatus(),
//                newGame.getMaxPlayers(),
//                newGame.getMaxRounds(),
//                newGame.getRoundDuration(),
//                newGame.getCommandCollectDuration(), // wird gelöscht
//                newGame.getCreatedGameDateTime(),
//            )
//
//            return ResponseEntity(responseGame, HttpStatus.CREATED)
//        } catch (e: InputMismatchException) {
//            throw ResponseStatusException(
//                HttpStatus.NOT_ACCEPTABLE,
//                "Invalid input, object invalid."
//            )
//        }
//    }
//
//    @PostMapping("/games/{gameId}/gameCommands/start")
//    fun startGame(
//       @PathVariable(value = "gameId") gameId: UUID,
//
//    ): ResponseEntity<HttpStatus> {
//        try {
//
//            gameService.runGame(gameId)
//            return ResponseEntity(HttpStatus.ACCEPTED)
//        } catch (e: EntityNotFoundException) {
//            throw ResponseStatusException(
//                HttpStatus.INTERNAL_SERVER_ERROR,
//
//            )
//        }
//    }
//
//
//    @PostMapping("/games/{gameId}/gameCommands/end")
//    fun endGame(
//        @PathVariable(value = "gameId") gameId: UUID,
//    ): ResponseEntity<HttpStatus> {
//        try {
//
//            gameService.closeGame(gameId)
//            return ResponseEntity(HttpStatus.ACCEPTED)
//        } catch (e: EntityNotFoundException) {
//            throw ResponseStatusException(
//                HttpStatus.INTERNAL_SERVER_ERROR,
//
//            )
//        }
//    }
//
//
//    @PatchMapping("/games/{id}/maxRounds/{maxRounds}")
//    fun updateEmployeePartially(@PathVariable id: UUID, @PathVariable maxRounds: Int):ResponseEntity<HttpStatus>  {
//        return try {
//            gameService.patchMaxRound(id, maxRounds)
//            ResponseEntity(HttpStatus.OK)
//        } catch (e: Exception) {
//            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
//        }
//    }
//
//    @PatchMapping("/games/{id}/roundDuration/{newDuration}")
//    fun updateEmployeePartially(@PathVariable id: UUID, @PathVariable newDuration: Long):ResponseEntity<HttpStatus> {
//        return try {
//            gameService.patchRoundDuration(id, newDuration)
//            ResponseEntity(HttpStatus.OK)
//        } catch (e: Exception) {
//            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
//        }
//    }

}








