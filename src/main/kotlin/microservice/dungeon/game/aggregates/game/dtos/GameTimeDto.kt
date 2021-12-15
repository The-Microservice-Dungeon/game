package microservice.dungeon.game.aggregates.game.dtos

class GameTimeDto(
    val currentGameTimeInMinutes: Long,
    val roundTimeInSeconds: Long,
    val currentRoundCount: Int
) {
}