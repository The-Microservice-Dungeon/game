package microservice.dungeon.game.integrationtests.model.player

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29098"
])
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29098", "port=29098"])
class PlayerRepositoryTests @Autowired constructor(
    private val playerRepository: PlayerRepository
) {
    private val ANY_PLAYER = Player("ANY_USERNAME", "ANY_MAILADDRESS")

    @BeforeEach
    fun setUp() {
        playerRepository.deleteAll()
    }

    @Test
    fun shouldSaveAndFindPlayerById() {
        // given
        playerRepository.save(ANY_PLAYER)

        // when
        val foundPlayer = playerRepository.findById(ANY_PLAYER.getPlayerId()).get()

        // then
        assertThat(foundPlayer)
            .isSameAs(ANY_PLAYER)
    }

    @Test
    fun shouldFindPlayerByToken() {

    }

    @Test
    fun shouldFindPlayerByMailAddressOrUserName() {

    }

    @Test
    fun shouldHaveUniqueUserName() {

    }

    @Test
    fun shouldHaveUniqueToken() {

    }

    @Test
    fun shouldHaveUniqueMailAddress() {

    }
}