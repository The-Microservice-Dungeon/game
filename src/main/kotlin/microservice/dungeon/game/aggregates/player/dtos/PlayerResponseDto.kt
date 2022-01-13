package microservice.dungeon.game.aggregates.player.dtos

import microservice.dungeon.game.aggregates.player.domain.Player
import java.util.*

class PlayerResponseDto(
    val bearerToken: UUID?,
    val name: String,
    val email: String
) {
    companion object {
        fun makeFromPlayer(player: Player): PlayerResponseDto =
            PlayerResponseDto(
                player.getPlayerToken(), player.getUserName(), player.getMailAddress()
            )
    }

    override fun toString(): String {
        return "PlayerResponseDto(bearerToken=$bearerToken, name='$name', email='$email')"
    }
}