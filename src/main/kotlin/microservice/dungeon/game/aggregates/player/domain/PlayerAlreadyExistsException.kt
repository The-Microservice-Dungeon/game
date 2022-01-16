package microservice.dungeon.game.aggregates.player.domain

class PlayerAlreadyExistsException: Exception("Player with same username or mailaddress already exists") {
}