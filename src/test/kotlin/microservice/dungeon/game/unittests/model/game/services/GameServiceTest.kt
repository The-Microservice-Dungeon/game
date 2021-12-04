package microservice.dungeon.game.unittests.model.game.services

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.events.AbstractGameEvent
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.servives.GameService
import microservice.dungeon.game.aggregates.player.events.PlayerCreated
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.util.*

class GameServiceTest {
    private val ANY_MAXPLAYERS = 99
    private val ANY_MAXROUNDS = 99
    private val ANY_GAME = Game(maxPlayers = ANY_MAXPLAYERS, maxRounds =   ANY_MAXROUNDS)

    private var mockEventPublisherService: EventPublisherService? = null
    private var mockEventStoreService: EventStoreService? = null
    private var mockGameRepository: GameRepository? = null
    private var mockPlayerRepository: PlayerRepository? = null
    private var mockRoundService: RoundService? = null
    private var gameService: GameService? = null

    //TODO(everything commented)

    @BeforeEach
    fun setUp() {

        mockEventPublisherService = mock()
        mockEventStoreService = mock()
        mockGameRepository = mock()
        mockPlayerRepository = mock()
        mockRoundService = mock()
        gameService = GameService(mockRoundService!!,mockGameRepository!!,mockPlayerRepository!! ,  mockEventStoreService!! ,  mockEventPublisherService!!)
    }


    @Test
    fun shouldAllowNewPlayerCreation() {
        // given
        // when
        val game: Game = gameService!!.createNewGame(ANY_GAME)

        // then
        verify(mockGameRepository!!).save(game)
        assertThat(game)
            .isCreatedFrom(maxPlayers = ANY_MAXPLAYERS, maxRounds =   ANY_MAXROUNDS)
    }


    @Test
    fun shouldPublishEventWhenNewPlayerCreated() {
        // given
        // when
        val game: Game = gameService!!.createNewGame(ANY_GAME)

        // then
        val gameCreatedCaptor = argumentCaptor<List<Event>>()
        verify(mockEventPublisherService!!).publishEvents(gameCreatedCaptor.capture())
        val capturedEvent = gameCreatedCaptor.firstValue.first()

        assertThat(capturedEvent)
            .isInstanceOf(PlayerCreated::class.java)
        assertThat(capturedEvent as AbstractGameEvent)
//            .matches(game)
        assertThat(capturedEvent.getOccurredAt().getTime())
            .isBeforeOrEqualTo(LocalDateTime.now())
    }

    @Test
    fun shouldStorePublishedEventWhenNewPlayerCreated() {
        // given
        // when
        val game: Game = gameService!!.createNewGame(ANY_GAME)

        // then
        val gameCreatedCaptor = argumentCaptor<Event>()
        verify(mockEventStoreService!!).storeEvent(gameCreatedCaptor.capture())
        val capturedEvent = gameCreatedCaptor.firstValue

        assertThat(capturedEvent)
 //           .isInstanceOf(GameCreated::class.java)
        assertThat(capturedEvent as AbstractGameEvent)
  //          .matches(game)
        assertThat(capturedEvent.getOccurredAt().getTime())
            .isBeforeOrEqualTo(LocalDateTime.now())
    }





}