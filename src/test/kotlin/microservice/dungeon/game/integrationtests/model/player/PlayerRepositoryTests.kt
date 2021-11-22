package microservice.dungeon.game.integrationtests.model.player

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
        // given
        playerRepository.save(ANY_PLAYER)

        // when
        val foundPlayer = playerRepository.findByPlayerToken(ANY_PLAYER.getPlayerToken()).get()

        // then
        assertThat(foundPlayer)
            .isSameAs(ANY_PLAYER)
    }

    @Test
    fun shouldFindPlayerByUserName() {
        // given
        playerRepository.save(ANY_PLAYER)

        // when
        val foundByUserName = playerRepository.findByUserNameOrMailAddress(ANY_PLAYER.getUserName(), "").get()

        // then
        assertThat(foundByUserName)
            .isSameAs(ANY_PLAYER)
    }

    @Test
    fun shouldFindPlayerByMailAddress() {
        // given
        playerRepository.save(ANY_PLAYER)

        // when
        val foundByMailAddress = playerRepository.findByUserNameOrMailAddress("", ANY_PLAYER.getMailAddress()).get()

        // then
        assertThat(foundByMailAddress)
            .isSameAs(ANY_PLAYER)
    }

    @Test
    fun shouldHaveUniqueUserName() {
        // given
        val playerWithSameUserName = Player(ANY_PLAYER.getUserName(), "SOME_OTHER_MAIL")
        playerRepository.save(ANY_PLAYER)

        // when then
        assertThatThrownBy {
            playerRepository.save(playerWithSameUserName)
        }
    }

    @Test
    fun shouldHaveUniqueToken() {

    }

    @Test
    fun shouldHaveUniqueMailAddress() {

    }
}