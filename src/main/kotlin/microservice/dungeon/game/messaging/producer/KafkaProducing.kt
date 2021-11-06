package microservice.dungeon.game.messaging.producer

interface KafkaProducing {

    fun send(topic: String, payload: String)
}