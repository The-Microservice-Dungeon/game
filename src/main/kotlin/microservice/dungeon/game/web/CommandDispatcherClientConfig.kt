package microservice.dungeon.game.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommandDispatcherClientConfig @Autowired constructor(
    @Value(value = "\${rest.robot.baseurl}") private val robotBaseUrl: String
) {
    @Bean
    fun roundCommandDispatcherClient(): CommandDispatcherClient = CommandDispatcherClient(robotBaseUrl)
}