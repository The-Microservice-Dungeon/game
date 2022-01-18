package microservice.dungeon.game.aggregates.game.controller

import microservice.dungeon.game.aggregates.game.controller.dto.*
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameNotFoundException
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
        logger.debug("Request to create new game received ...")
        logger.trace("POST: /games")
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
        logger.trace("POST /games/$gameId/gameCommands/start")

        return try {
            val transactionId: UUID = gameService.startGame(gameId)

            logger.debug("Request to start game was successful. [gameId=$gameId]")
            logger.trace("Responding with 201.")
            ResponseEntity(HttpStatus.CREATED)

        } catch (e: GameNotFoundException) {
            logger.warn("Request to start game failed. Game not found. [gameId=$gameId]")
            logger.warn(e.message)
            logger.trace("Responding with 404")
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: Exception) {
            logger.warn("Request to start game failed. [gameId=$gameId]")
            logger.warn(e.message)
            logger.trace("Responding with 403")
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @PostMapping("/games/{gameId}/gameCommands/end")
    fun endGame(@PathVariable(name = "gameId") gameId: UUID): ResponseEntity<HttpStatus> {
        logger.debug("Request to end game received ... [gameId=$gameId]")
        logger.trace("POST /games/$gameId/gameCommands/end")

        return try {
            gameService.endGame(gameId)

            logger.debug("Request to end game was successful. [gameId=$gameId]")
            logger.trace("Responding with 201.")
            ResponseEntity(HttpStatus.CREATED)

        } catch (e: GameNotFoundException) {
            logger.warn("Request to end game failed. Game not found. [gameId=$gameId]")
            logger.warn(e.message)
            logger.trace("Responding with 404")
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: Exception) {
            logger.warn("Request to end game failed. [gameId=$gameId]")
            logger.warn(e.message)
            logger.trace("Responding with 403")
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @PutMapping("/games/{gameId}/players/{playerToken}", produces = ["application/json"])
    fun joinGame(@PathVariable(name = "gameId") gameId: UUID, @PathVariable(name = "playerToken") playerToken: UUID): ResponseEntity<JoinGameResponseDto> {
        logger.debug("Request to join game received ... [gameId=${gameId}]")
        logger.trace("PUT /games/$gameId/players/xxx")

        return try {
            val transactionId: UUID = gameService.joinGame(playerToken, gameId)
            val responseDto = JoinGameResponseDto(transactionId)

            logger.debug("Request to join game was successful. [gameId=${gameId}]")
            logger.trace("Responding with 200")
            ResponseEntity(responseDto, HttpStatus.OK)

        } catch (e: GameNotFoundException) {
            logger.warn("Request to join game failed. Game not found. [gameId=${gameId}]")
            logger.warn(e.message)
            logger.trace("Responding with 404")
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: PlayerNotFoundException) {

            logger.warn("Request to join game failed. Player not found. [gameId=${gameId}]")
            logger.warn(e.message)
            logger.trace("Responding with 404")
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: Exception) {
            logger.warn("Request to join game failed. Action not allowed. [gameId=${gameId}]")
            logger.warn(e.message)
            logger.trace("Responding with 403")
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @GetMapping("/games/{gameId}/time", produces = ["application/json"])
    fun getGameTime(@PathVariable(name = "gameId") gameId: UUID): ResponseEntity<GameTimeResponseDto> {
        logger.debug("Received request to fetch game-time. [gameId=$gameId]")
        logger.trace("GET /games/$gameId/time")

        return try {
            val game: Game = gameRepository.findById(gameId).get()
            val responseBody = GameTimeResponseDto(game)

            logger.debug("Request to fetch game-time successful.")
            logger.trace("Responding with 200. Serialized ResponseBody is:")
            logger.trace(responseBody.serialize())
            ResponseEntity(responseBody, HttpStatus.OK)

        } catch (e: Exception) {
            logger.warn("Request to fetch game-time failed. Game not found.")
            logger.trace("Responding with 404")
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/games", produces = ["application/json"])
    fun getGames(): List<GameResponseDto> {
        logger.debug("Received request to fetch active Games.")
        logger.trace("GET /games")

        val response: List<GameResponseDto> = gameRepository.findAllByGameStatusIn(listOf(GameStatus.CREATED, GameStatus.GAME_RUNNING))
            .map { game -> GameResponseDto(game) }

        logger.debug("Request successful. Responding with:")
        response.forEach { responseDto ->
            logger.trace(responseDto.serialize())
        }
        return response
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
