package microservice.dungeon.game.aggregates.player.controller.dtos

import microservice.dungeon.game.aggregates.player.domain.Player
import java.util.*

data class CreatePlayerRequestDto(
    val name: String,
    val email: String
) {

}
