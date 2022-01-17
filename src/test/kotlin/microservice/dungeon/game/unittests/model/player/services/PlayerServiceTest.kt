package microservice.dungeon.game.unittests.model.player.services

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.domain.PlayerAlreadyExistsException
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.player.services.PlayerService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

class PlayerServiceTest {
    private var mockPlayerRepository: PlayerRepository? = null
    private var playerService: PlayerService? = null

    @BeforeEach
    fun setUp() {
        mockPlayerRepository = mock()
        playerService = PlayerService(mockPlayerRepository!!)
    }

    @Test
    fun shouldAllowToCreateNewPlayer() {
        // given
        val userName = "dadepu"
        val mailAddress = "dadepu@smail.th-koeln.de"

        // when
        val responsePlayer: Player = playerService!!.createNewPlayer(userName, mailAddress)

        // then
        verify(mockPlayerRepository!!).save(responsePlayer)
    }

    @Test
    fun shouldThrowWhenPlayerAlreadyExistsWhilePlayerCreation() {
        // given
        val player = Player("anyName", "anyMail")
        whenever(mockPlayerRepository!!.findByUserNameOrMailAddress(player.getUserName(), player.getMailAddress()))
            .thenReturn(Optional.of(player))

        // when, then
        assertThrows(PlayerAlreadyExistsException::class.java) {
            playerService!!.createNewPlayer(player.getUserName(), player.getMailAddress())
        }
    }

}