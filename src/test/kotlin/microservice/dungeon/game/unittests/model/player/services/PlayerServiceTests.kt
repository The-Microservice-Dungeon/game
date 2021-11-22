package microservice.dungeon.game.unittests.model.player.services

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.events.AbstractPlayerEvent
import microservice.dungeon.game.aggregates.player.events.PlayerCreated
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.player.services.PlayerService
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.util.*

class PlayerServiceTests {
    private val ANY_USERNAME = "ANY_USERNAME"
    private val ANY_MAILADDRESS = "ANY_MAILADDRESS"
    private val ANY_PLAYER = Player(ANY_USERNAME, ANY_MAILADDRESS)

    private var mockEventPublisherService: EventPublisherService? = null
    private var mockEventStoreService: EventStoreService? = null
    private var mockPlayerRepository: PlayerRepository? = null
    private var playerService: PlayerService? = null


    @BeforeEach
    fun setUp() {
        mockEventPublisherService = mock()
        mockEventStoreService = mock()
        mockPlayerRepository = mock()
        playerService = PlayerService(mockPlayerRepository!!, mockEventPublisherService!!, mockEventStoreService!!)
    }


    @Test
    fun shouldAllowNewPlayerCreation() {
        // given
        // when
        val player: Player = playerService!!.createNewPlayer(ANY_USERNAME, ANY_MAILADDRESS)

        // then
        verify(mockPlayerRepository!!).save(player)
        assertThat(player)
            .isCreatedFrom(ANY_USERNAME, ANY_MAILADDRESS)
    }

    @Test
    fun shouldNotAllowPlayerCreationWhenUserNameOrMailAddressAlreadyExists() {
        // given
        whenever(mockPlayerRepository!!.findPlayerByUserNameOrMailAddress(anyString(), anyString()))
            .thenReturn(Optional.of(
                ANY_PLAYER
            ))

        // when, then
        assertThatThrownBy {
            playerService!!.createNewPlayer(ANY_USERNAME, ANY_MAILADDRESS)
        }
    }

    @Test
    fun shouldPublishEventWhenNewPlayerCreated() {
        // given
        // when
        val player: Player = playerService!!.createNewPlayer(ANY_USERNAME, ANY_MAILADDRESS)

        // then
        val playerCreatedCaptor = argumentCaptor<List<Event>>()
        verify(mockEventPublisherService!!).publishEvents(playerCreatedCaptor.capture())
        val capturedEvent = playerCreatedCaptor.firstValue.first()

        assertThat(capturedEvent)
            .isInstanceOf(PlayerCreated::class.java)
        assertThat(capturedEvent as AbstractPlayerEvent)
            .matches(player)
        assertThat(capturedEvent.getOccurredAt().getTime())
            .isBeforeOrEqualTo(LocalDateTime.now())
    }

    @Test
    fun shouldStorePublishedEventWhenNewPlayerCreated() {
        // given
        // when
        val player: Player = playerService!!.createNewPlayer(ANY_USERNAME, ANY_MAILADDRESS)

        // then
        val playerCreatedCaptor = argumentCaptor<Event>()
        verify(mockEventStoreService!!).storeEvent(playerCreatedCaptor.capture())
        val capturedEvent = playerCreatedCaptor.firstValue

        assertThat(capturedEvent)
            .isInstanceOf(PlayerCreated::class.java)
        assertThat(capturedEvent as AbstractPlayerEvent)
            .matches(player)
        assertThat(capturedEvent.getOccurredAt().getTime())
            .isBeforeOrEqualTo(LocalDateTime.now())
    }
}