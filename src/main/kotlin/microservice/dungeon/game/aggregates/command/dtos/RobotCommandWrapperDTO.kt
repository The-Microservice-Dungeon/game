package microservice.dungeon.game.aggregates.command.dtos

class RobotCommandWrapperDTO(
    val commands: List<String>
) {
    companion object {
        fun makeFromDTOList(commands: List<Any>) = RobotCommandWrapperDTO(
            commands.map { x -> x.toString() }
        )
    }
}