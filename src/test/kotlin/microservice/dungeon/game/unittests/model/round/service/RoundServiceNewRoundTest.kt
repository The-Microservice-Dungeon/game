package microservice.dungeon.game.unittests.model.round.service

import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.AbstractRoundEvent
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
import microservice.dungeon.game.assertions.CustomAssertions
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.*

class RoundServiceNewRoundTest {
    private var mockEventStoreService: EventStoreService? = null
    private var mockEventPublisherService: EventPublisherService? = null
    private var mockRobotCommandDispatcherClient: RobotCommandDispatcherClient? = null
    private var mockRoundRepository: RoundRepository? = null
    private var mockCommandRepository: CommandRepository? = null
    private var roundService: RoundService? = null

    private val ANY_ROUND_ID = UUID.randomUUID()
    private val ANY_GAMEID = UUID.randomUUID()
    private val ANY_ROUND_NUMBER = 3


    @BeforeEach
    fun setUp() {
        mockEventStoreService = mock()
        mockEventPublisherService = mock()
        mockRobotCommandDispatcherClient = mock()
        mockRoundRepository = mock()
        mockCommandRepository = mock()
        roundService = RoundService(mockRoundRepository!!, mockCommandRepository!!, mockEventStoreService!!, mockEventPublisherService!!, mockRobotCommandDispatcherClient!!)
    }



    @Test
    fun shouldAllowNewRoundCreation() {
        // given
        // when
        val roundId = roundService!!.startNewRound(ANY_GAMEID, ANY_ROUND_NUMBER)

        // then
        argumentCaptor<Round>().apply {
            verify(mockRoundRepository!!).save(capture())

            val round = firstValue
            Assertions.assertThat(round.getGameId())
                .isEqualTo(ANY_GAMEID)
            Assertions.assertThat(round.getRoundNumber())
                .isEqualTo(ANY_ROUND_NUMBER)
            Assertions.assertThat(round.getRoundStatus())
                .isEqualTo(RoundStatus.COMMAND_INPUT_STARTED)
        }
    }

    @Test
    fun shouldStoreRoundStartedWhenNewRoundCreated() {
        var round: Round? = null
        var roundStarted: Event? = null

        // given
        // when
        val roundId = roundService!!.startNewRound(ANY_GAMEID, ANY_ROUND_NUMBER)


        // then
        argumentCaptor<Round>().apply {
            verify(mockRoundRepository!!).save(capture())
            round = firstValue
        }
        argumentCaptor<Event>().apply {
            verify(mockEventStoreService!!).storeEvent(capture())
            roundStarted = firstValue
        }
        CustomAssertions.assertThat(roundStarted!!)
            .isInstanceOf(RoundStarted::class.java)
        Assertions.assertThat(roundStarted!!.getTransactionId())
            .isEqualTo(roundId)
        CustomAssertions.assertThat(roundStarted!! as AbstractRoundEvent)
            .matches(round!!)
    }

    @Test
    fun shouldPublishRoundStartedWhenNewRoundCreated() {
        var round: Round? = null
        var roundStarted: Event? = null

        // given
        // when
        val roundId = roundService!!.startNewRound(ANY_GAMEID, ANY_ROUND_NUMBER)

        // then
        argumentCaptor<Round>().apply {
            verify(mockRoundRepository!!).save(capture())
            round = firstValue
        }
        argumentCaptor<List<Event>>().apply {
            verify(mockEventPublisherService!!).publishEvents(capture())
            roundStarted = firstValue.first()
        }
        CustomAssertions.assertThat(roundStarted!!)
            .isInstanceOf(RoundStarted::class.java)
        Assertions.assertThat(roundStarted!!.getTransactionId())
            .isEqualTo(roundId)
        CustomAssertions.assertThat(roundStarted!! as AbstractRoundEvent)
            .matches(round!!)
    }

    @Test
    fun shouldNotAllowNewRoundCreationWhenSameRoundAlreadyExists() {
        // given
        val duplicateGameId = ANY_GAMEID
        val duplicateRoundNumber = ANY_ROUND_NUMBER
        whenever(mockRoundRepository!!.findByGameIdAndRoundNumber(duplicateGameId, duplicateRoundNumber))
            .thenReturn(Optional.of(Round(duplicateGameId, duplicateRoundNumber)))

        // when then
        Assertions.assertThatThrownBy {
            roundService!!.startNewRound(duplicateGameId, duplicateRoundNumber)
        }
        verify(mockRoundRepository!!, never()).save(any())
    }
}