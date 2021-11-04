package microservice.dungeon.game.aggregates.core

interface EventPublishing: Event {
    fun serializeEvent(): String

    fun initializeFromSerializedEvent(event: String)

    fun getTopic(): String
}