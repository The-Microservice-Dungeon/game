package microservice.dungeon.game.aggregates.player.controller

import microservice.dungeon.game.aggregates.player.controller.dtos.CreatePlayerRequestDto
import microservice.dungeon.game.aggregates.player.controller.dtos.PlayerResponseDto
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.domain.PlayerAlreadyExistsException
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.player.services.PlayerService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class PlayerController @Autowired constructor(
    private val playerService: PlayerService,
    private val playerRepository: PlayerRepository
){
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostMapping("/players", consumes = ["application/json"], produces = ["application/json"])
    fun createNewPlayer(@RequestBody requestPlayer: CreatePlayerRequestDto): ResponseEntity<PlayerResponseDto> {
        logger.debug("REST-Request to create new Player received ... [playerName={}]", requestPlayer.name)

        return try {
            val newPlayer = playerService.createNewPlayer(requestPlayer.name, requestPlayer.email)
            val responsePlayer = PlayerResponseDto.makeFromPlayer(newPlayer)

            logger.debug("Request successful. Player created. [playerName={}, playerId={}]", newPlayer.getUserName(), newPlayer.getPlayerId())
            logger.trace("Responding with 201.")
            ResponseEntity(responsePlayer, HttpStatus.CREATED)

        } catch (e: Exception) {
            logger.debug("Request to create new player failed. [playerName={}", requestPlayer.name)
            logger.debug(e.message)
            logger.trace("Responding with 403.")
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @GetMapping("/players", produces = ["application/json"])
    fun getPlayer(@RequestParam(name = "name") userName: String, @RequestParam(name = "mail") userMail: String): ResponseEntity<PlayerResponseDto> {
        logger.debug("REST-Request to fetch Player-Details received ... [playerName={}]", userName)

        return try {
            val player: Player = playerRepository.findByUserNameAndMailAddress(userName, userMail).get()
            val responsePlayer = PlayerResponseDto.makeFromPlayer(player)

            logger.debug("Request successful. Player found. [playerName={}]", player.getUserName())
            logger.trace("Responding with 200.")
            ResponseEntity(responsePlayer, HttpStatus.OK)

        } catch (e: Exception) {
            logger.debug("Request to fetch Player-Details failed. [playerName={}]", userName)
            logger.debug(e.message)
            logger.trace("Responding with 404.")
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
