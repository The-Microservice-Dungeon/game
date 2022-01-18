package microservice.dungeon.game.aggregates.round.web.dto

class RobotCommandWrapperDto(
    val commands: List<String>
) {
    companion object {
        fun makeFromDTOList(commands: List<Any>) = RobotCommandWrapperDto(
            commands.map { x -> x.toString() }
        )
    }
}