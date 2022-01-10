package microservice.dungeon.game.aggregates.game.web.dto

class NewGameWorldDto(
    val gameworld: NewGameWorldInnerDto
) {
    companion object {
        fun makeFromNumberOfPlayer(numberOfPlayer: Int): NewGameWorldDto {
            return NewGameWorldDto(NewGameWorldInnerDto(numberOfPlayer))
        }
    }

    override fun equals(other: Any?): Boolean =
        (other is NewGameWorldDto) && gameworld == other.gameworld

    override fun hashCode(): Int = gameworld.hashCode()
}