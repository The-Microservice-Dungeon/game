package microservice.dungeon.game.unittests.model.player.domain

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlayerTests {
    private val ANY_USERNAME = "ANY_USERNAME"
    private val ANY_MAILADDRESS = "ANY_MAILADDRESS"
    private val ANY_FIRSTNAME = "ANY_FIRSTNAME"
    private val ANY_LASTNAME = "ANY_LASTNAME"



    @Test
    fun shouldInitializeValidObject() {
        // given
        val player = Player(ANY_USERNAME, ANY_MAILADDRESS, ANY_FIRSTNAME, ANY_LASTNAME)

        // when
        // then
        assertThat(player)
            .isCreatedFrom(ANY_USERNAME, ANY_MAILADDRESS, ANY_FIRSTNAME, ANY_LASTNAME)
        assertThat(player.getPlayerId())
            .isNotNull
        assertThat(player.getPlayerToken())
            .isNotNull
    }

    @Test
    fun shouldNotHaveEqualPlayerIdAndPlayerToken() {
        // given
        val player = Player(ANY_USERNAME, ANY_MAILADDRESS, ANY_FIRSTNAME, ANY_LASTNAME)

        // when
        // then
        assertThat(player.getPlayerId())
            .isNotEqualTo(player.getPlayerToken())
    }

    @Test
    fun shouldNotInitializeWithoutValidUserName() {
        TODO("implement username pattern validation")
    }

    @Test
    fun shouldNotInitializeWithoutValidMailAddress() {
        TODO("implement mailAddress pattern validation")
    }
}