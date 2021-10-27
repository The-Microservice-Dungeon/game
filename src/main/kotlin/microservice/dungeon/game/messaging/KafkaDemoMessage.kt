package microservice.dungeon.game.messaging

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class KafkaDemoMessage @Autowired constructor(

    private val kafkaProducer: KafkaProducer
): ApplicationListener<ContextRefreshedEvent>{

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        println("Sending message: demo message @testTopic")
        kafkaProducer.send("testTopic", "demo message")
    }
}