package microservice.dungeon.game.aggregates.core

interface EventDto {
    fun serialize(): String
}