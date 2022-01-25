package microservice.dungeon.game.aggregates.player.domain

class PlayerAlreadyExistsException(playerName: String): Exception("Failed to create Player. $playerName already exists.") {
}