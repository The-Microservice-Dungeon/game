package microservice.dungeon.game.aggregates.commands.domain

class RoundCommands(
    val list: List<Command>,
    val roundNumber: Int
)