package microservice.dungeon.game.aggregates.game.controller

import microservice.dungeon.game.aggregates.game.controller.dto.*
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameNotFoundException
import microservice.dungeon.game.aggregates.game.domain.GameStateException
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.servives.GameService
import microservice.dungeon.game.aggregates.player.domain.PlayerNotFoundException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


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
        logger.debug("REST-Request to create new Game received ...")
        logger.trace{requestGame.toString()}

        return try {
            val response: Pair<UUID, Game> = gameService
                .createNewGame(requestGame.maxPlayers, requestGame.maxRounds)
            val responseDto = CreateGameResponseDto(response.second.getGameId())

            logger.debug("Request to create new Game was successful. [gameId={}]", response.second.getGameId())
            logger.trace("Responding with 201. Serialized ResponseBody is:")
            logger.trace{responseDto.toString()}
            ResponseEntity(responseDto, HttpStatus.CREATED)

        } catch (e: Exception) {
            logger.debug("Request to create new Game failed.")
            logger.debug{e.message}
            logger.trace("Responding with 403.")
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @PostMapping("/games/{gameId}/gameCommands/start")
    fun startGame(@PathVariable(name = "gameId") gameId: UUID): ResponseEntity<HttpStatus> {
        logger.debug("REST-Request to start Game received ... [gameId={}]", gameId)

        return try {
            val transactionId: UUID = gameService.startGame(gameId)

            logger.debug("Request to start Game was successful. [gameId={}]", gameId)
            logger.trace("Responding with 201.")
            ResponseEntity(HttpStatus.CREATED)

        } catch (e: GameNotFoundException) {
            logger.debug("Request to start Game failed. Game not found. [gameId={}]", gameId)
            logger.debug{e.message}
            logger.trace("Responding with 404")
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: Exception) {
            logger.debug("Request to start game failed. [gameId={}]", gameId)
            logger.debug{e.message}
            logger.trace("Responding with 403")
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @PostMapping("/games/{gameId}/gameCommands/end")
    fun endGame(@PathVariable(name = "gameId") gameId: UUID): ResponseEntity<HttpStatus> {
        logger.debug("REST-Request to end Game received ... [gameId={}]", gameId)

        return try {
            gameService.endGame(gameId)

            logger.debug("Request to end game was successful. [gameId={}]", gameId)
            logger.trace("Responding with 201.")
            ResponseEntity(HttpStatus.CREATED)

        } catch (e: GameNotFoundException) {
            logger.debug("Request to end game failed. Game not found. [gameId={}]", gameId)
            logger.debug{e.message}
            logger.trace("Responding with 404")
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: Exception) {
            logger.debug("Request to end game failed. [gameId={}]", gameId)
            logger.debug{e.message}
            logger.trace("Responding with 403")
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @PutMapping("/games/{gameId}/players/{playerToken}", produces = ["application/json"])
    fun joinGame(@PathVariable(name = "gameId") gameId: UUID, @PathVariable(name = "playerToken") playerToken: UUID): ResponseEntity<JoinGameResponseDto> {
        logger.debug("REST-Request to join Game received ... [gameId={}]", gameId)

        return try {
            val transactionId: UUID = gameService.joinGame(playerToken, gameId)
            val responseDto = JoinGameResponseDto(transactionId)

            logger.debug("Request to join game was successful. [gameId={}]", gameId)
            logger.trace("Responding with 200")
            ResponseEntity(responseDto, HttpStatus.OK)

        } catch (e: GameNotFoundException) {
            logger.debug("Request to join game failed. Game not found.[gameId={}]", gameId)
            logger.debug{e.message}
            logger.trace("Responding with 404")
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: PlayerNotFoundException) {
            logger.debug("Request to join game failed. Player not found. [gameId={}]", gameId)
            logger.debug{e.message}
            logger.trace("Responding with 404")
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: Exception) {
            logger.debug("Request to join game failed. Action not allowed. [gameId={}]", gameId)
            logger.debug{e.message}
            logger.trace("Responding with 403")
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @GetMapping("/games/{gameId}/time", produces = ["application/json"])
    fun getGameTime(@PathVariable(name = "gameId") gameId: UUID): ResponseEntity<GameTimeResponseDto> {
        logger.debug("REST-Request to fetch Game-Time received... [gameId={}]", gameId)

        return try {
            val game: Game = gameRepository.findById(gameId).get()
            val responseBody = GameTimeResponseDto(game)

            logger.debug("Request to fetch Game-Time successful.")
            logger.trace("Responding with 200. Serialized ResponseBody is:")
            logger.trace{responseBody.toString()}
            ResponseEntity(responseBody, HttpStatus.OK)

        } catch (e: Exception) {
            logger.debug("Request to fetch Game-Time failed. Game not found.")
            logger.trace("Responding with 404")
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/games", produces = ["application/json"])
    fun getGames(): List<GameResponseDto> {
        logger.debug("REST-Request to fetch active Games received...")

        val response: List<GameResponseDto> = gameRepository.findAllByGameStatusIn(listOf(GameStatus.CREATED, GameStatus.GAME_RUNNING))
            .map { game -> GameResponseDto(game) }

        logger.debug("Request successful. Responding with:")
        response.forEach { responseDto ->
            logger.trace(responseDto.serialize())
        }
        return response
    }

    @PatchMapping("/games/{gameId}/maxRounds")
    fun patchMaximumNumberOfRounds(@PathVariable(name = "gameId") gameId: UUID, @RequestBody dto: PatchGameMaxRoundsDto): ResponseEntity<HttpStatus> {
        logger.debug("REST-Request to change maximum number of rounds received ... [gameId=$gameId, maxRounds=${dto.maxRounds}]")

        return try {
            gameService.changeMaximumNumberOfRounds(gameId, dto.maxRounds)
            logger.debug("Request successful. Changed maximum number of rounds to ${dto.maxRounds}.")
            logger.trace("Responding with 200.")
            ResponseEntity(HttpStatus.OK)

        } catch (e: GameNotFoundException) {
            logger.debug("Request failed. Game not found. [gameId=$gameId]")
            logger.debug(e.message)
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: GameStateException) {
            logger.debug("Request failed. Game is not in a state that allows it to be changed.")
            logger.debug(e.message)
            ResponseEntity(HttpStatus.FORBIDDEN)

        } catch (e: IllegalArgumentException) {
            logger.debug("Request failed. Requested change not allowed due to some constraints.")
            logger.debug(e.message)
            ResponseEntity(HttpStatus.FORBIDDEN)

        } catch (e: Exception) {
            logger.error("Request failed unexpectedly.")
            logger.error(e.message)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PatchMapping("/games/{gameId}/duration")
    fun patchRoundDuration(@RequestBody dto: PatchGameDurationDto, @PathVariable(name = "gameId") gameId: UUID): ResponseEntity<HttpStatus> {
        logger.debug("Received request to change round duration ... [gameId=$gameId, duration=${dto.duration}]")

        return try {
            gameService.changeRoundDuration(gameId, dto.duration)
            logger.debug("Request successful. Changed round duration to ${dto.duration} (in Millis).")
            logger.trace("Responding with 200.")
            ResponseEntity(HttpStatus.OK)

        } catch (e: GameNotFoundException) {
            logger.debug("Request failed. Game not found. [gameId=$gameId]")
            logger.debug(e.message)
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: GameStateException) {
            logger.debug("Request failed. Game is not in a state that allows it to be changed.")
            logger.debug(e.message)
            ResponseEntity(HttpStatus.FORBIDDEN)

        } catch (e: IllegalArgumentException) {
            logger.debug("Request failed. Requested change is not allowed due to constraints.")
            logger.debug(e.message)
            ResponseEntity(HttpStatus.FORBIDDEN)

        } catch (e: Exception) {
            logger.error("Request failed unexpectedly.")
            logger.error(e.message)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
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
