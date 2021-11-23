package microservice.dungeon.game.messaging.producer

import microservice.dungeon.game.aggregates.core.Event

interface KafkaProducing {

    fun send(topic: String, payload: String)

    fun send(event: Event)
}