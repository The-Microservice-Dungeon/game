package microservice.dungeon.game.aggregates.player.domain

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class PlayerAlreadyExistsException: Exception("Player with same username or mailaddress already exists") {
}