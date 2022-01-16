package microservice.dungeon.game.integrationtests.model.player.repository

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import java.util.*

@SpringBootTest(properties = [
    "kafka.bootstrapAddress=localhost:29101"
])
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29101", "port=29101"])
class PlayerRepositoryIntegrationTest @Autowired constructor(
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
    fun shouldHaveUniqueMailAddress() {
        // given
        val playerWithSameMailAddress = Player("SOME_OTHER_USERNAME", ANY_PLAYER.getMailAddress())
        playerRepository.save(ANY_PLAYER)

        // when then
        assertThatThrownBy {
            playerRepository.save(playerWithSameMailAddress)
        }
    }

    @Test
    fun shouldHaveUniqueToken() {
        // given
        val playerWithSameToken = Player(UUID.randomUUID(), ANY_PLAYER.getPlayerToken(), "SOME_OTHER_USERNAME", "SOME_OTHER_MAILADDRESS")
        playerRepository.save(ANY_PLAYER)

        // when then
        assertThat(playerWithSameToken.getPlayerToken())
            .isEqualTo(ANY_PLAYER.getPlayerToken())
        assertThatThrownBy {
            playerRepository.save(playerWithSameToken)
        }
    }

    @Test
    fun shouldAllowToFindPlayerByUserNameAndMailAddress() {
        // given
        val player1 = Player("dadepu1", "dadepu1")
        val player2 = Player("dadepu2", "dadepu2")
        playerRepository.saveAll(listOf(player1, player2))

        // when
        val capturedPlayer1: Player = playerRepository.findByUserNameAndMailAddress(
            player1.getUserName(), player1.getMailAddress()).get()

        // then
        assertThat(capturedPlayer1.getPlayerId())
            .isEqualTo(player1.getPlayerId())

        // and when
        val capturedPlayer2: Optional<Player> = playerRepository.findByUserNameAndMailAddress(player2.getUserName(), "some other mail")

        // then
        assertThat(capturedPlayer2)
            .isEmpty
    }
}