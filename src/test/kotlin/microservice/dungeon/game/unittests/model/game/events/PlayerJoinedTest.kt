package microservice.dungeon.game.unittests.model.game.events

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.events.PlayerJoined
import microservice.dungeon.game.aggregates.player.domain.Player
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class PlayerJoinedTest {

    private val ANY_GAME = Game(maxRounds = 100, maxPlayers = 10)
    private val ANY_PLAYER = Player("dadepu", "dadepu@th-koeln.de")

    @Test
    fun shouldContainTransactionId() {
        // give
        val transactionId = UUID.randomUUID()

        // when
        val playerJoined = PlayerJoined(ANY_GAME, ANY_PLAYER, transactionId)

        // then
        assertThat(playerJoined.getTransactionId())
            .isEqualTo(transactionId)
    }
}