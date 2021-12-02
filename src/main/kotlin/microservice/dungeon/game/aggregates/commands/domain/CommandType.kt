package microservice.dungeon.game.aggregates.commands.domain

enum class CommandType {
    BLOCKING,
    MINING,
    MOVEMENT,
    BATTLE,
    BUYING,
    SELLING,
    REGENERATE,
    BATTLEITEMUSE,
    REPAIRITEMUSE,
    MOVEITEMUSE
}