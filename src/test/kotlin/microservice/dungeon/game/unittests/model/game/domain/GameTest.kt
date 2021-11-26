package microservice.dungeon.game.unittests.model.game.domain

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GameTest {
    private val ANY_USERNAME = "ANY_USERNAME"
    private val ANY_MAILADDRESS = "ANY_MAILADDRESS"


    @Test
    fun shouldInitializeValidObject() {
        // given
        val player = Player(ANY_USERNAME, ANY_MAILADDRESS)

        // when
        // then
        assertThat(player)
            .isCreatedFrom(ANY_USERNAME, ANY_MAILADDRESS)
        assertThat(player.getPlayerId())
            .isNotNull
        assertThat(player.getPlayerToken())
            .isNotNull
        assertThat(player.getUserName())
            .isEqualTo(ANY_USERNAME)
        assertThat(player.getMailAddress())
            .isEqualTo(ANY_MAILADDRESS)
    }

    @Test
    fun shouldNotHaveEqualPlayerIdAndPlayerToken() {
        // given
        val player = Player(ANY_USERNAME, ANY_MAILADDRESS)

        // when
        // then
        assertThat(player.getPlayerId())
            .isNotEqualTo(player.getPlayerToken())
    }

    @Test
    @Disabled
    fun shouldNotInitializeWithoutValidUserName() {
        TODO("implement username pattern validation")
    }

    @Test
    @Disabled
    fun shouldNotInitializeWithoutValidMailAddress() {
        TODO("implement mailAddress pattern validation")
    }
}