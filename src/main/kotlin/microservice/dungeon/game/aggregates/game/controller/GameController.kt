package microservice.dungeon.game.aggregates.game.controller

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.core.EntityNotFoundException
import microservice.dungeon.game.aggregates.core.GameAlreadyFullException
import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.dtos.GameResponseDto
import microservice.dungeon.game.aggregates.game.dtos.GameTimeDto
import microservice.dungeon.game.aggregates.game.dtos.PlayerJoinGameDto
import microservice.dungeon.game.aggregates.game.servives.GameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.validation.Valid


@RestController
@RequestMapping("/")
class GameController(@Autowired private val gameService: GameService) {

    @GetMapping("/games")
    fun getAllGames(): MutableIterable<Game> = gameService.getAllGames()

    @GetMapping("/games/{gameId}/time")
    fun getTime(@PathVariable(value = "gameId") gameId: UUID, @ModelAttribute game: Game): GameTimeDto {
        return gameService.getGameTime(gameId)
    }


    @PutMapping("/games/{gameId}/players/{playerToken}")
    fun insertPlayerById (
        @PathVariable(value = "gameId") gameId: UUID,
        @PathVariable(value = "playerToken") playerToken: UUID
    ): ResponseEntity<PlayerJoinGameDto> {
        try {
            return gameService.insertPlayer(gameId, playerToken)

        } catch (e: EntityNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Player not found"
            )
        } catch (e: MethodNotAllowedForStatusException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Wrong game status"
            )
        } catch (e: GameAlreadyFullException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Game already full"
            )
        } catch (e: EntityAlreadyExistsException) {
            throw ResponseStatusException(
                HttpStatus.NOT_ACCEPTABLE,
                "Player already in Game"
            )
        }
    }

    @PostMapping("/games", consumes = ["application/json"], produces = ["application/json"])
    fun createNewGame(@RequestBody game: Game): ResponseEntity<GameResponseDto> {
        try {
            val newGame = gameService.createNewGame(game)

            val responseGame = GameResponseDto(
                newGame.getGameId(),
                newGame.getGameStatus(),
                newGame.getMaxPlayers(),
                newGame.getMaxRounds(),
                newGame.getRoundDuration(),
                newGame.getCommandCollectDuration(), // wird gelöscht
                newGame.getCreatedGameDateTime(),
            )

            return ResponseEntity(responseGame, HttpStatus.CREATED)
        } catch (e: InputMismatchException) {
            throw ResponseStatusException(
                HttpStatus.NOT_ACCEPTABLE,
                "Invalid input, object invalid."
            )
        }
    }

    @PostMapping("/games/{gameId}/gameCommands/start")
    fun startGame(
       @PathVariable(value = "gameId") gameId: UUID,
//        @RequestBody (adminToken: UUID)
    ): ResponseEntity<HttpStatus> {
        try {
            //val gameId: UUID = UUID.fromString(uuid)
            //val admin: Admin = adminRepository.findByAdminToken(adminToken).get()
            //            ?: throw EntityNotFoundException("admin does not exist")
            gameService.runGame(gameId)
            return ResponseEntity(HttpStatus.ACCEPTED)
        } catch (e: EntityNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_ACCEPTABLE,
                "Admin only."
            )
        }
    }


    @PostMapping("/games/{gameId}/gameCommands/end")
    fun endGame(
        @PathVariable(value = "gameId") gameId: UUID,
        @RequestBody adminToken: UUID,
    ): ResponseEntity<HttpStatus> {
        try {
            //val admin: Admin = adminRepository.findByAdminToken(adminToken).get()
            //            ?: throw EntityNotFoundException("admin does not exist")
            gameService.closeGame(gameId)
            return ResponseEntity(HttpStatus.ACCEPTED)
        } catch (e: EntityNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_ACCEPTABLE,
                "Admin only."
            )
        }
    }


    @PatchMapping("/games/{id}/maxRounds/{maxRounds}")
    fun updateEmployeePartially(@PathVariable id: UUID, @PathVariable maxRounds: Int): ResponseEntity<GameResponseDto?>? {
        return try {
            gameService.patchMaxRound(id, maxRounds)

        } catch (e: Exception) {
            ResponseEntity<GameResponseDto?>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PatchMapping("/games/{id}/roundDuration/{newDuration}")
    fun updateEmployeePartially(@PathVariable id: UUID, @PathVariable newDuration: Long): ResponseEntity<GameResponseDto?>? {
        return try {
            gameService.patchRoundDuration(id, newDuration)

        } catch (e: Exception) {
            ResponseEntity<GameResponseDto?>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}








