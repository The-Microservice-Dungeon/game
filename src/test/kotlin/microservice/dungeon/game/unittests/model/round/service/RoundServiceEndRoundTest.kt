package microservice.dungeon.game.unittests.model.round.service

import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundNotFoundException
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStatusEvent
import microservice.dungeon.game.aggregates.round.events.RoundStatusEventBuilder
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
import microservice.dungeon.game.aggregates.round.web.TradingCommandDispatcherClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.kotlin.*
import java.util.*

class RoundServiceEndRoundTest {
    private var mockEventStoreService: EventStoreService? = null
    private var mockEventPublisherService: EventPublisherService? = null
    private var mockRobotCommandDispatcherClient: RobotCommandDispatcherClient? = null
    private var mockGameRepository: GameRepository? = null
    private var mockTradingCommandDispatcherClient: TradingCommandDispatcherClient? = null
    private var mockRoundRepository: RoundRepository? = null
    private var mockCommandRepository: CommandRepository? = null
    private var roundService: RoundService? = null

    private val roundStatusEventBuilder = RoundStatusEventBuilder("anyTopic", "anyType", 1)

    private val game = Game(10, 100)
    private val roundId = UUID.randomUUID()
    private val roundNumber = 3


    @BeforeEach
    fun setUp() {
        mockEventStoreService = mock()
        mockEventPublisherService = mock()
        mockRobotCommandDispatcherClient = mock()
        mockTradingCommandDispatcherClient = mock()
        mockGameRepository = mock()
        mockRoundRepository = mock()
        mockCommandRepository = mock()
        roundService = RoundService(
            mockRoundRepository!!,
            mockCommandRepository!!,
            mockEventStoreService!!,
            mockGameRepository!!,
            mockEventPublisherService!!,
            mockRobotCommandDispatcherClient!!,
            mockTradingCommandDispatcherClient!!,
            roundStatusEventBuilder
        )
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class
    )
    fun shouldAllowToEndRoundRegardlessOfRoundStatus(roundStatus: RoundStatus) {
        // given
        val spyRound = spy(Round(game = game, roundNumber = roundNumber, roundId = roundId, roundStatus = roundStatus))
        whenever(mockRoundRepository!!.findById(roundId))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.endRound(roundId)

        // then
        verify(spyRound).endRound()
        verify(mockRoundRepository!!).save(check {
            assertThat(it)
                .isEqualTo(spyRound)
            assertThat(it.getRoundStatus())
                .isEqualTo(RoundStatus.ROUND_ENDED)
        })
    }

    @Test
    fun shouldThrowWhenRoundNotExists() {
        // given
        whenever(mockRoundRepository!!.findById(any()))
            .thenReturn(Optional.empty())

        // when then
        assertThrows(RoundNotFoundException::class.java) {
            roundService!!.endRound(UUID.randomUUID())
        }

        verify(mockRoundRepository!!, never()).save(any())
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["ROUND_ENDED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun shouldPublishWhenRoundEnded(roundStatus: RoundStatus) {
        // given
        val round = Round(game = game, roundNumber = roundNumber, roundId = roundId, roundStatus = roundStatus)
        whenever(mockRoundRepository!!.findById(round.getRoundId()))
            .thenReturn(Optional.of(round))

        // when
        roundService!!.endRound(roundId)

        // then
        verify(mockEventStoreService!!).storeEvent(check { event: RoundStatusEvent ->
            assertThat(event.roundId)
                .isEqualTo(round.getRoundId())
            assertThat(RoundStatus.ROUND_ENDED)
                .isEqualTo(round.getRoundStatus())
            assertThat(event.roundNumber)
                .isEqualTo(round.getRoundNumber())
        })
        verify(mockEventPublisherService!!).publishEvent(check { event: RoundStatusEvent ->
            assertThat(event.roundId)
                .isEqualTo(round.getRoundId())
            assertThat(RoundStatus.ROUND_ENDED)
                .isEqualTo(round.getRoundStatus())
            assertThat(event.roundNumber)
                .isEqualTo(round.getRoundNumber())
        })
    }

    @Test
    fun shouldNotPublishWhenRoundAlreadyEnded() {
        // given
        val round = Round(game = game, roundNumber = roundNumber, roundId = roundId, roundStatus = RoundStatus.ROUND_ENDED)
        whenever(mockRoundRepository!!.findById(any()))
            .thenReturn(Optional.of(round))

        // when
        roundService!!.endRound(roundId)

        // then
        verify(mockEventStoreService!!, never()).storeEvent(any())
        verify(mockEventPublisherService!!, never()).publishEvents(any())
    }
}