package microservice.dungeon.game.aggregates.player.controller

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.player.dtos.PlayerResponseDto
import microservice.dungeon.game.aggregates.player.services.PlayerService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class PlayerController @Autowired constructor(
    private val playerService: PlayerService
){

    private val logger = KotlinLogging.logger {}

    @PostMapping("/players", consumes = ["application/json"], produces = ["application/json"])
    fun createNewPlayer(@RequestBody requestPlayer: PlayerResponseDto): ResponseEntity<PlayerResponseDto> {
        logger.info("Create-New-Player request received.")
        logger.trace("{}", requestPlayer.toString())

        val newPlayer = playerService.createNewPlayer(requestPlayer.name, requestPlayer.email)
        val responsePlayer = PlayerResponseDto.makeFromPlayer(newPlayer)
        return ResponseEntity(responsePlayer, HttpStatus.CREATED)
    }
}