package microservice.dungeon.game.unittests.model.player.events

import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.events.PlayerCreated
import microservice.dungeon.game.aggregates.player.events.PlayerCreatedBuilder
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class PlayerCreatedTests {
    private val ANY_PLAYER = Player("ANY_USERNAME", "ANY_MAILADDRESS", "ANY_FIRSTNAME", "ANY_LASTNAME")

    @Test
    fun shouldConstructFromPlayer() {
        // given
        // when
        val playerCreated = PlayerCreated(ANY_PLAYER)

        // then
        assertThat(playerCreated)
            .matches(ANY_PLAYER)
    }
}