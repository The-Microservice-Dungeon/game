package microservice.dungeon.game.messaging

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaTopicConfig @Autowired constructor(
    @Value("\${kafka.topicMock}")
    private val topicMock: String,
    @Value("\${kafka.topicProdRound}")
    private val topicProdRound: String,
    @Value("\${kafka.topicProdGame}")
    private val topicProdGame: String,
    @Value("\${kafka.topicProdPlayer}")
    private val topicProdPlayer: String
) {

    @Value(value = "\${kafka.bootstrapAddress}")
    private val bootstrapAddress: String = ""

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        return KafkaAdmin(configs)
    }

    @Bean
    fun testTopic(): NewTopic = NewTopic("testTopic", 1, 1)

    @Bean
    fun mockTopic(): NewTopic = NewTopic(topicMock, 1, 1)

    @Bean
    fun prodRoundTopic(): NewTopic = NewTopic(topicProdRound, 1, 1)

    @Bean
    fun prodGameTopic(): NewTopic = NewTopic(topicProdGame, 1, 1)

    @Bean
    fun prodPlayerTopic(): NewTopic = NewTopic(topicProdPlayer, 1, 1)
}