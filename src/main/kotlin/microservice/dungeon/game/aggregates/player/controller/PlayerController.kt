package microservice.dungeon.game.aggregates.player.controller

import microservice.dungeon.game.aggregates.player.controller.dtos.PlayerResponseDto
import microservice.dungeon.game.aggregates.player.domain.PlayerAlreadyExistsException
import microservice.dungeon.game.aggregates.player.services.PlayerService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PlayerController @Autowired constructor(
    private val playerService: PlayerService
){
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostMapping("/players", consumes = ["application/json"], produces = ["application/json"])
    fun createNewPlayer(@RequestBody requestPlayer: PlayerResponseDto): ResponseEntity<PlayerResponseDto> {
        logger.debug("Request to create new player received ... [playerName=${requestPlayer.name}]")

        return try {
            val newPlayer = playerService.createNewPlayer(requestPlayer.name, requestPlayer.email)
            val responsePlayer = PlayerResponseDto.makeFromPlayer(newPlayer)

            logger.debug("Request successful. Player created. [playerName=${newPlayer.getUserName()}, playerId=${newPlayer.getPlayerId()}]")
            logger.trace("Responding with 201.")
            ResponseEntity(responsePlayer, HttpStatus.CREATED)

        } catch (e: Exception) {
            logger.warn("Request to create new player failed.")
            logger.warn(e.message)
            logger.trace("Responding with 403.")
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }
}