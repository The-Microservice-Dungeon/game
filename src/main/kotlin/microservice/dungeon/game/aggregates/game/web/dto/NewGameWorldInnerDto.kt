package microservice.dungeon.game.aggregates.game.web.dto

class NewGameWorldInnerDto(
    val player_amount: Int
) {
    override fun equals(other: Any?): Boolean =
        (other is NewGameWorldInnerDto) && player_amount == other.player_amount
}