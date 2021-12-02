package microservice.dungeon.game.aggregates.command.domain

class RoundCommands(
    val list: List<Command>,
    val roundNumber: Int
)