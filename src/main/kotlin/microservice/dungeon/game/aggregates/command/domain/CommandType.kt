package microservice.dungeon.game.aggregates.command.domain

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