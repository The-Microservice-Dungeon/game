package microservice.dungeon.game.unittests.model.round.service

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.AbstractRoundEvent
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.assertions.CustomAssertions
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.kotlin.*
import java.util.*

class RoundServiceTests {
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


    @Test
    fun shouldAllowNewRoundCreation() {
        // given
        // when
        val roundId = roundService!!.startNewRound(ANY_GAMEID, ANY_ROUND_NUMBER)

        // then
        argumentCaptor<Round>().apply {
            verify(mockRoundRepository!!).save(capture())

            val round = firstValue
            assertThat(round.getGameId())
                .isEqualTo(ANY_GAMEID)
            assertThat(round.getRoundNumber())
                .isEqualTo(ANY_ROUND_NUMBER)
            assertThat(round.getRoundStatus())
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
        assertThat(roundStarted!!)
            .isInstanceOf(RoundStarted::class.java)
        assertThat(roundStarted!!.getTransactionId())
            .isEqualTo(roundId)
        assertThat(roundStarted!! as AbstractRoundEvent)
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
        assertThat(roundStarted!!)
            .isInstanceOf(RoundStarted::class.java)
        assertThat(roundStarted!!.getTransactionId())
            .isEqualTo(roundId)
        assertThat(roundStarted!! as AbstractRoundEvent)
            .matches(round!!)
    }

    @Test
    fun shouldSendPassiveScoutingCommandToRobotWhenNewRoundCreated() {
        //TODO("blocked till commands are available and robot restapi is stable")

    }

    @Test
    fun shouldNotAllowNewRoundCreationWhenSameRoundAlreadyExists() {
        // given
        val duplicateGameId = ANY_GAMEID
        val duplicateRoundNumber = ANY_ROUND_NUMBER
        whenever(mockRoundRepository!!.findByGameIdAndRoundNumber(duplicateGameId, duplicateRoundNumber))
            .thenReturn(Optional.of(Round(duplicateGameId, duplicateRoundNumber)))

        // when then
        assertThatThrownBy {
            roundService!!.startNewRound(duplicateGameId, duplicateRoundNumber)
        }
    }



    @Test
    fun shouldAllowEndCommandInput() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.COMMAND_INPUT_STARTED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.endCommandInputs(ANY_GAMEID)

        // then
        verify(spyRound).endCommandInputPhase()
        verify(mockRoundRepository!!).save(isA<Round>())
    }

    @Test
    fun shouldStoreCommandInputEndedWhenCommandInputEnded() {
        var roundCommandInputEnded: Round? = null
        var commandInputEnded: Event? = null

        // given
        val roundCommandInputStarted = Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.COMMAND_INPUT_STARTED)
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(roundCommandInputStarted))

        // when
        roundService!!.endCommandInputs(ANY_GAMEID)

        // then
        argumentCaptor<Round>().apply {
            verify(mockRoundRepository!!).save(capture())
            roundCommandInputEnded = firstValue
        }
        argumentCaptor<Event>().apply {
            verify(mockEventStoreService!!).storeEvent(capture())
            commandInputEnded = firstValue
        }
        assertThat(commandInputEnded!!)
            .isInstanceOf(CommandInputEnded::class.java)
        assertThat(commandInputEnded!!.getTransactionId())
            .isEqualTo(roundCommandInputStarted.getRoundId())
        assertThat(commandInputEnded!! as AbstractRoundEvent)
            .matches(roundCommandInputEnded!!)
    }

    @Test
    fun shouldPublishCommandInputEndedWhenCommandInputEnded() {
        var roundCommandInputEnded: Round? = null
        var commandInputEnded: Event? = null

        // given
        val roundCommandInputStarted = Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.COMMAND_INPUT_STARTED)
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(roundCommandInputStarted))

        // when
        roundService!!.endCommandInputs(ANY_GAMEID)

        // then
        argumentCaptor<Round>().apply {
            verify(mockRoundRepository!!).save(capture())
            roundCommandInputEnded = firstValue
        }
        argumentCaptor<List<Event>>().apply {
            verify(mockEventPublisherService!!).publishEvents(capture())
            commandInputEnded = firstValue.first()
        }
        assertThat(commandInputEnded!!)
            .isInstanceOf(CommandInputEnded::class.java)
        assertThat(commandInputEnded!!.getTransactionId())
            .isEqualTo(roundCommandInputStarted.getRoundId())
        assertThat(commandInputEnded!! as AbstractRoundEvent)
            .matches(roundCommandInputEnded!!)
    }

    @Test
    fun shouldNotAllowEndCommandInputWhenRoundNotExists() {
        // given
        whenever(mockRoundRepository!!.findById(isA<UUID>()))
            .thenReturn(Optional.empty())

        // when then
        assertThatThrownBy {
            roundService!!.endCommandInputs(ANY_ROUND_ID)
        }
    }

    @Test
    fun shouldNotPublishOrStoreEventWhenCommandInputAlreadyEnded() {
        // given
        val commandInputAlreadyEndedRound = Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.COMMAND_INPUT_ENDED)
        whenever(mockRoundRepository!!.findById(any()))
            .thenReturn(Optional.of(commandInputAlreadyEndedRound))

        // when then
        assertThatThrownBy {
            roundService!!.endCommandInputs(ANY_ROUND_ID)
        }
        verify(mockEventPublisherService!!, never()).publishEvents(any())
        verify(mockEventStoreService!!, never()).storeEvent(any())
    }




    @Test
    fun shouldAllowDispatchBlockingCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.COMMAND_INPUT_ENDED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.deliverBlockingCommands(ANY_GAMEID)

        // then
        verify(spyRound).deliverBlockingCommandsToRobot()
        verify(mockRoundRepository!!).save(isA<Round>())
    }

    @Test
    fun shouldNotAllowDispatchBlockingCommandsWhenRoundNotExists() {
        //TODO
    }

    @Test
    fun shouldSendBlockingCommandsToRobotWhenDispatchingBlockingCommands() {
        //TODO
    }




    @Test
    fun shouldAllowDispatchTradingCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.BLOCKING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.deliverTradingCommands(ANY_GAMEID)

        // then
        verify(spyRound).deliverTradingCommandsToRobot()
        verify(mockRoundRepository!!).save(isA<Round>())
    }

    @Test
    fun shouldNotAllowDispatchTradingCommandsWhenRoundNotExists() {
        //TODO
    }

    @Test
    fun shouldSendTradingCommandsToRobotWhenDispatchingTradingCommands() {
        //TODO
    }




    @Test
    fun shouldAllowDispatchMovementCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.TRADING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.deliverMovementCommands(ANY_GAMEID)

        // then
        verify(spyRound).deliverMovementCommandsToRobot()
        verify(mockRoundRepository!!).save(isA<Round>())
    }

    @Test
    fun shouldNotAllowDispatchMovementCommandsWhenRoundNotExists() {
        //TODO
    }

    @Test
    fun shouldSendMovementCommandsToRobotWhenDispatchingMovementCommands() {
        //TODO
    }




    @Test
    fun shouldAllowDispatchBattleCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.MOVEMENT_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.deliverBattleCommands(ANY_GAMEID)

        // then
        verify(spyRound).deliverBattleCommandsToRobot()
        verify(mockRoundRepository!!).save(isA<Round>())
    }

    @Test
    fun shouldNotAllowDispatchBattleCommandsWhenRoundNotExists() {
        //TODO
    }

    @Test
    fun shouldSendBattleCommandsToRobotWhenDispatchingBattleCommands() {
        //TODO
    }




    @Test
    fun shouldAllowDispatchMiningCommands() {

    }

    @Test
    fun shouldNotAllowDispatchMiningCommandsWhenRoundNotExists() {
        //TODO
    }

    @Test
    fun shouldSendMiningCommandsToRobotWhenDispatchingMiningCommands() {
        //TODO
    }







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
        assertThat(capturedRound!!.getRoundId())
            .isEqualTo(round.getRoundId())
        assertThat(capturedRound.getRoundStatus())
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
        assertThat(roundEnded!!)
            .isInstanceOf(RoundEnded::class.java)
        assertThat(roundEnded!!.getTransactionId())
            .isEqualTo(round.getRoundId())
        assertThat(roundEnded!! as AbstractRoundEvent)
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
        assertThat(roundEnded!!)
            .isInstanceOf(RoundEnded::class.java)
        assertThat(roundEnded!!.getTransactionId())
            .isEqualTo(round.getRoundId())
        assertThat(roundEnded!! as AbstractRoundEvent)
            .matches(round)
    }

    @Test
    fun shouldNotAllowEndRoundWhenRoundNotExists() {
        // given
        whenever(mockRoundRepository!!.findById(any()))
            .thenReturn(Optional.empty())

        // when then
        assertThatThrownBy {
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