package microservice.dungeon.game.aggregates.player.controller

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.player.dtos.PlayerResponseDto
import microservice.dungeon.game.aggregates.player.services.PlayerService
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

    @PostMapping("/players", consumes = ["application/json"], produces = ["application/json"])
    fun createNewPlayer(@RequestBody requestPlayer: PlayerResponseDto): ResponseEntity<PlayerResponseDto> {
            val newPlayer = playerService.createNewPlayer(requestPlayer.name, requestPlayer.email)
            val responsePlayer = PlayerResponseDto.makeFromPlayer(newPlayer)
            return ResponseEntity(responsePlayer, HttpStatus.CREATED)
    }
}