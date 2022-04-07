package microservice.dungeon.game.aggregates.player.controller.dtos

import microservice.dungeon.game.aggregates.player.domain.Player
import java.util.*

class PlayerResponseDto(
    val playerId: UUID,
    val bearerToken: UUID,
    val name: String,
    val email: String
) {
    companion object {
        fun makeFromPlayer(player: Player): PlayerResponseDto =
            PlayerResponseDto(
                player.getPlayerId(), player.getPlayerToken(), player.getUserName(), player.getMailAddress()
            )
    }

    override fun toString(): String {
        return "PlayerResponseDto(playerId=$playerId, bearerToken=$bearerToken, name='$name', email='$email')"
    }
}
