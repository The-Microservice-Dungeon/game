package microservice.dungeon.game.aggregates.player.dtos

import java.util.*

class PlayerResponseDto(
    val bearerToken: UUID?,
    val name: String,
    val email: String
) {
}