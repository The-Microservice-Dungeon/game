package microservice.dungeon.game.aggregates.game.controller

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.servives.GameService
import microservice.dungeon.game.aggregates.player.Player.Player
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/")
class GameController(@Autowired private val gameService: GameService) {

    @GetMapping("/games")
    fun getAllGames(): MutableIterable<Game> = gameService.getAllGames()

    @GetMapping("/games/{gameId}/time")
    fun getTime(@PathVariable(value = "gameId") gameId: UUID, @ModelAttribute game: Game) = game.getCurrentTime()


    @PutMapping("/games/{gameId}/players")
    fun insertPlayerById(@PathVariable(value = "gameId") gameId: UUID,
                          @Valid @RequestBody newPlayer: Player
    ) = gameService.insertPlayer(gameId, newPlayer)


    @PostMapping("/games")
    fun createNewGame(): Game = gameService.createNewGame()


    @PostMapping("/games/{gameId}/gameCommands/start")
    fun startGame(@PathVariable(value = "gameId") gameId: UUID, @ModelAttribute game: Game) = gameService.runGame(gameId)


    @PostMapping("/games/{gameId}/gameCommands/end")
    fun endGame(@PathVariable(value = "gameId") gameId: UUID, @ModelAttribute game: Game) = gameService.closeGame(gameId)

}








