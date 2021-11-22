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
    private val ANY_PLAYER = Player("ANY_USERNAME", "ANY_MAILADDRESS")

    @Test
    fun shouldConstructFromPlayer() {
        // given
        // when
        val playerCreated = PlayerCreated(ANY_PLAYER)

        // then
        assertThat(playerCreated)
            .matches(ANY_PLAYER)
    }

    @Test
    fun shouldBeEqualByValue() {
        // given
        val eventId = UUID.randomUUID()
        val eventTime = EventTime.makeFromLocalDateTime(LocalDateTime.now())
        val playerCreatedOne = PlayerCreated(eventId, eventTime, ANY_PLAYER.getPlayerId(), ANY_PLAYER.getUserName(), ANY_PLAYER.getMailAddress())
        val playerCreatedTwo = PlayerCreated(eventId, eventTime, ANY_PLAYER.getPlayerId(), ANY_PLAYER.getUserName(), ANY_PLAYER.getMailAddress())

        // when
        // then
        assertThat(playerCreatedOne)
            .isEqualTo(playerCreatedTwo)
    }

    @Test
    fun shouldBeSerializableAndReConstructable() {
        // given
        val playerCreated = PlayerCreated(ANY_PLAYER)

        //when
        val serializedPlayerCreated = playerCreated.serialized()
        val deserializedPlayerCreated = PlayerCreatedBuilder().deserializedEvent(serializedPlayerCreated)

        // then
        assertThat(deserializedPlayerCreated)
            .isEqualTo(playerCreated)
    }
}