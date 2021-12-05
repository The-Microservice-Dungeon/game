package microservice.dungeon.game.unittests.model.round.service

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.AbstractRoundEvent
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.assertions.CustomAssertions
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.kotlin.*
import java.util.*

class RoundServiceEndRoundTest {
    private var mockEventStoreService: EventStoreService? = null
    private var mockEventPublisherService: EventPublisherService? = null
    private var mockRoundRepository: RoundRepository? = null
    private var roundService: RoundService? = null

    private val ANY_ROUND_ID = UUID.randomUUID()
    private val ANY_GAMEID = UUID.randomUUID()
    private val ANY_ROUND_NUMBER = 3


    @BeforeEach
    fun setUp() {
        mockEventStoreService = mock()
        mockEventPublisherService = mock()
        mockRoundRepository = mock()
        roundService = RoundService(mockRoundRepository!!, mockEventStoreService!!, mockEventPublisherService!!)
    }


    // END ROUND

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class
    )
    fun shouldAllowEndRoundRegardlessOfRoundStatus(roundStatus: RoundStatus) {
        val capturedRound: Round?

        // given
        val round = Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, roundStatus)
        whenever(mockRoundRepository!!.findById(any()))
            .thenReturn(Optional.of(round))

        // when
        roundService!!.endRound(ANY_ROUND_ID)

        // then
        argumentCaptor<Round>().apply {
            verify(mockRoundRepository!!).save(capture())
            capturedRound = firstValue
        }
        Assertions.assertThat(capturedRound!!.getRoundId())
            .isEqualTo(round.getRoundId())
        Assertions.assertThat(capturedRound.getRoundStatus())
            .isEqualTo(RoundStatus.ROUND_ENDED)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["ROUND_ENDED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun shouldPublishRoundEndedWhenRoundEnded(roundStatus: RoundStatus) {
        var roundEnded: Event?

        // given
        val round = Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, roundStatus)
        whenever(mockRoundRepository!!.findById(any()))
            .thenReturn(Optional.of(round))

        // when
        roundService!!.endRound(ANY_ROUND_ID)

        // then
        argumentCaptor<List<Event>>().apply {
            verify(mockEventPublisherService!!).publishEvents(capture())
            roundEnded = firstValue.first()
        }
        CustomAssertions.assertThat(roundEnded!!)
            .isInstanceOf(RoundEnded::class.java)
        Assertions.assertThat(roundEnded!!.getTransactionId())
            .isEqualTo(round.getRoundId())
        CustomAssertions.assertThat(roundEnded!! as AbstractRoundEvent)
            .matches(round)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["ROUND_ENDED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun shouldStoreRoundEndedWhenRoundEnded(roundStatus: RoundStatus) {
        var roundEnded: Event?

        // given
        val round = Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, roundStatus)
        whenever(mockRoundRepository!!.findById(any()))
            .thenReturn(Optional.of(round))

        // when
        roundService!!.endRound(ANY_ROUND_ID)

        // then
        argumentCaptor<Event>().apply {
            verify(mockEventStoreService!!).storeEvent(capture())
            roundEnded = firstValue
        }
        CustomAssertions.assertThat(roundEnded!!)
            .isInstanceOf(RoundEnded::class.java)
        Assertions.assertThat(roundEnded!!.getTransactionId())
            .isEqualTo(round.getRoundId())
        CustomAssertions.assertThat(roundEnded!! as AbstractRoundEvent)
            .matches(round)
    }

    @Test
    fun shouldNotAllowEndRoundWhenRoundNotExists() {
        // given
        whenever(mockRoundRepository!!.findById(any()))
            .thenReturn(Optional.empty())

        // when then
        Assertions.assertThatThrownBy {
            roundService!!.endRound(ANY_ROUND_ID)
        }
        verify(mockRoundRepository!!, never()).save(any())
    }

    @Test
    fun shouldNotPublishOrStoreEventWhenRoundAlreadyEnded() {
        // given
        val round = Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.ROUND_ENDED)
        whenever(mockRoundRepository!!.findById(any()))
            .thenReturn(Optional.of(round))

        // when
        roundService!!.endRound(ANY_ROUND_ID)

        // then
        verify(mockEventStoreService!!, never()).storeEvent(any())
        verify(mockEventPublisherService!!, never()).publishEvents(any())
    }
}