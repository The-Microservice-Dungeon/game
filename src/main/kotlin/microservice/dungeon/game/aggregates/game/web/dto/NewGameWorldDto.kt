package microservice.dungeon.game.aggregates.game.web.dto

class NewGameWorldDto(
    val gameworld: NewGameWorldInnerDto
) {
    companion object {
        fun makeDefault(): NewGameWorldDto {
            return NewGameWorldDto(NewGameWorldInnerDto(1))
        }
    }

    override fun equals(other: Any?): Boolean =
        (other is NewGameWorldDto) && gameworld == other.gameworld
}