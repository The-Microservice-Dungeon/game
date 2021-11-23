package microservice.dungeon.game.aggregates.game.domain

enum class GameStatus {
    CREATED,
    IN_PREPARATION,
    GAME_RUNNING,
    GAME_FINISHED
}