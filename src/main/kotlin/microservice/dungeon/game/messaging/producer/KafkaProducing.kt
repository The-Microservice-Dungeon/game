package microservice.dungeon.game.messaging.producer

import microservice.dungeon.game.aggregates.core.Event
import org.apache.kafka.clients.producer.ProducerRecord

interface KafkaProducing {

    fun send(event: Event)

    fun send(record: ProducerRecord<String, String>)
}