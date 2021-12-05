package microservice.dungeon.game.unittests.model.round.service

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.AbstractRoundEvent
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.*

class RoundServiceTest {
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



    // End Command Inputs

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
    fun shouldStoreCommandInputEndedEventWhenCommandInputEnded() {
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
    fun shouldPublishCommandInputEndedEventWhenCommandInputEnded() {
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
        verify(mockRoundRepository!!, never()).save(any())
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





    // Dispatch BLOCKING Commands

    @Test
    fun shouldAllowToDispatchBlockingCommands() {
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
    fun shouldNotAllowToDispatchBlockingCommandsWhenRoundNotExists() {
        // given
        whenever(mockRoundRepository!!.findById(isA<UUID>()))
            .thenReturn(Optional.empty())

        // when then
        assertThatThrownBy {
            roundService!!.deliverBlockingCommands(ANY_ROUND_ID)
        }
        verify(mockRoundRepository!!, never()).save(any())
    }

    @Test
    fun shouldSendBlockingCommandsToRobotWhenDispatchingBlockingCommands() {
        //TODO
    }





    // Dispatch TRADING Commands

    @Test
    fun shouldAllowToDispatchTradingCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.BLOCKING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.deliverTradingCommands(ANY_GAMEID)

        // then
        verify(spyRound).deliverSellingCommandsToRobot()
        verify(spyRound).deliverBuyingCommandsToRobot()
        verify(mockRoundRepository!!).save(isA<Round>())
    }

    @Test
    fun shouldNotAllowToDispatchTradingCommandsWhenRoundNotExists() {
        // given
        whenever(mockRoundRepository!!.findById(isA<UUID>()))
            .thenReturn(Optional.empty())

        // when then
        assertThatThrownBy {
            roundService!!.deliverTradingCommands(ANY_ROUND_ID)
        }
        verify(mockRoundRepository!!, never()).save(any())
    }

    @Test
    fun shouldSendSellingCommandsToRobotWhenDispatchingTradingCommands() {
        //TODO
    }

    @Test
    fun shouldSendBuyingCommandsToRobotWhenDispatchingTradingCommands() {
        //TODO
    }





    // Dispatch MOVEMENT Commands

    @Test
    fun shouldAllowToDispatchMovementCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.BUYING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.deliverMovementCommands(ANY_GAMEID)

        // then
        verify(spyRound).deliverMovementItemUseCommandsToRobot()
        verify(spyRound).deliverMovementCommandsToRobot()
        verify(mockRoundRepository!!).save(isA<Round>())
    }

    @Test
    fun shouldNotAllowToDispatchMovementCommandsWhenRoundNotExists() {
        // given
        whenever(mockRoundRepository!!.findById(isA<UUID>()))
            .thenReturn(Optional.empty())

        // when then
        assertThatThrownBy {
            roundService!!.deliverMovementCommands(ANY_ROUND_ID)
        }
        verify(mockRoundRepository!!, never()).save(any())
    }

    @Test
    fun shouldSendMovementItemUseCommandsToRobotWhenDispatchingMovementCommands() {
        //TODO
    }

    @Test
    fun shouldSendMovementCommandsToRobotWhenDispatchingMovementCommands() {
        //TODO
    }





    // Dispatch BATTLE Commands

    @Test
    fun shouldAllowToDispatchBattleCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.MOVEMENT_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.deliverBattleCommands(ANY_GAMEID)

        // then
        verify(spyRound).deliverBattleItemUseCommandsToRobot()
        verify(spyRound).deliverBattleCommandsToRobot()
        verify(mockRoundRepository!!).save(isA<Round>())
    }

    @Test
    fun shouldNotAllowToDispatchBattleCommandsWhenRoundNotExists() {
        // given
        whenever(mockRoundRepository!!.findById(isA<UUID>()))
            .thenReturn(Optional.empty())

        // when then
        assertThatThrownBy {
            roundService!!.deliverBattleCommands(ANY_ROUND_ID)
        }
        verify(mockRoundRepository!!, never()).save(any())
    }

    @Test
    fun shouldSendBattleItemUseCommandsToRobotWhenDispatchingBattleCommands() {
        //TODO
    }

    @Test
    fun shouldSendBattleCommandsToRobotWhenDispatchingBattleCommands() {
        //TODO
    }





    // Dispatch MINING Commands

    @Test
    fun shouldAllowDispatchMiningCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.BATTLE_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.deliverMiningCommands(ANY_GAMEID)

        // then
        verify(spyRound).deliverMiningCommandsToRobot()
        verify(mockRoundRepository!!).save(isA<Round>())
    }

    @Test
    fun shouldNotAllowDispatchMiningCommandsWhenRoundNotExists() {
        // given
        whenever(mockRoundRepository!!.findById(isA<UUID>()))
            .thenReturn(Optional.empty())

        // when then
        assertThatThrownBy {
            roundService!!.deliverMiningCommands(ANY_ROUND_ID)
        }
        verify(mockRoundRepository!!, never()).save(any())
    }

    @Test
    fun shouldSendMiningCommandsToRobotWhenDispatchingMiningCommands() {
        //TODO
    }





    // Dispatch REGENERATING Commands


}