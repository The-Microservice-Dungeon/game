package microservice.dungeon.game.aggregates.game.controller

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.dtos.GameResponseDto
import microservice.dungeon.game.aggregates.game.servives.GameService
import microservice.dungeon.game.aggregates.player.domain.Player
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
    fun getTime(@PathVariable(value = "gameId") gameId: UUID, @ModelAttribute game: Game) =
        gameService.getGameTime(gameId)


    @PutMapping("/games/{gameId}/players")
    fun insertPlayerById(
        @PathVariable(value = "gameId") gameId: UUID,
        @Valid @RequestBody playerToken: UUID
    ) = gameService.insertPlayer(gameId, playerToken)



    @PostMapping("/games", consumes = ["application/json"], produces = ["application/json"])
    fun createNewGame(@RequestBody requestGame: GameResponseDto): ResponseEntity<GameResponseDto> {
        try {
            val newGame = gameService.createNewGame()

            val responseGame = GameResponseDto(
                newGame.getGameId(),
                newGame.getGameStatus(),
                newGame.getMaxPlayers(),
                newGame.getMaxRounds(),
                newGame.getRoundDuration(),
                newGame.getCommandCollectDuration(),
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
    fun startGame(@PathVariable(value = "gameId") gameId: UUID, @ModelAttribute game: Game) =
        gameService.runGame(gameId)


    @PostMapping("/games/{gameId}/gameCommands/end")
    fun endGame(@PathVariable(value = "gameId") gameId: UUID) =
        gameService.closeGame(gameId)

}








