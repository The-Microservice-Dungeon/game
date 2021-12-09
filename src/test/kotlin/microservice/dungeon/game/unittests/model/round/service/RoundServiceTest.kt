package microservice.dungeon.game.unittests.model.round.service

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandObject
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.dtos.BlockCommandDTO
import microservice.dungeon.game.aggregates.command.dtos.UseItemMovementCommandDTO
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.AbstractRoundEvent
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
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
    private var mockRobotCommandDispatcherClient: RobotCommandDispatcherClient? = null
    private var mockRoundRepository: RoundRepository? = null
    private var mockCommandRepository: CommandRepository? = null
    private var roundService: RoundService? = null

    private val ANY_ROUND_ID = UUID.randomUUID()
    private val ANY_GAMEID = UUID.randomUUID()
    private val ANY_PLAYERID = UUID.randomUUID()
    private val ANY_ROBOTID = UUID.randomUUID()
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
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.COMMAND_INPUT_ENDED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.BLOCKING))
            .thenReturn(
                getListOfBlockingCommands(CommandType.BLOCKING)
            )

        // when
        roundService!!.deliverBlockingCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.BLOCKING)
        verify(mockRobotCommandDispatcherClient!!).sendBlockingCommands(any())
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
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.BUYING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.MOVEITEMUSE))
            .thenReturn(
                getListOfBlockingCommands(CommandType.MOVEITEMUSE)
            )

        // when
        roundService!!.deliverMovementCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.MOVEITEMUSE)
        verify(mockRobotCommandDispatcherClient!!).sendMovementItemUseCommands(any())
    }

    @Test
    fun shouldSendMovementCommandsToRobotWhenDispatchingMovementCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.BUYING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.MOVEMENT))
            .thenReturn(
                getListOfBlockingCommands(CommandType.MOVEMENT)
            )

        // when
        roundService!!.deliverMovementCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.MOVEMENT)
        verify(mockRobotCommandDispatcherClient!!).sendMovementCommands(any())
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
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.MOVEMENT_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.BATTLEITEMUSE))
            .thenReturn(
                getListOfBlockingCommands(CommandType.BATTLEITEMUSE)
            )

        // when
        roundService!!.deliverBattleCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.BATTLEITEMUSE)
        verify(mockRobotCommandDispatcherClient!!).sendBattleItemUseCommands(any())
    }

    @Test
    fun shouldSendBattleCommandsToRobotWhenDispatchingBattleCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.MOVEMENT_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.BATTLE))
            .thenReturn(
                getListOfBlockingCommands(CommandType.BATTLE)
            )

        // when
        roundService!!.deliverBattleCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.BATTLE)
        verify(mockRobotCommandDispatcherClient!!).sendBattleCommands(any())
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
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.BATTLE_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.MINING))
            .thenReturn(
                getListOfBlockingCommands(CommandType.MINING)
            )

        // when
        roundService!!.deliverMiningCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.MINING)
        verify(mockRobotCommandDispatcherClient!!).sendMiningCommands(any())
    }





    // Dispatch REGENERATING Commands

    @Test
    fun shouldAllowToDispatchRegeneratingCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.MINING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.deliverRegeneratingCommands(ANY_GAMEID)

        // then
        verify(spyRound).deliverRepairItemUseCommandsToRobot()
        verify(spyRound).deliverRegeneratingCommandsToRobot()
        verify(mockRoundRepository!!).save(isA<Round>())
    }

    @Test
    fun shouldNotAllowToDispatchRegeneratingCommandsWhenRoundNotExists() {
        // given
        whenever(mockRoundRepository!!.findById(isA<UUID>()))
            .thenReturn(Optional.empty())

        // when then
        assertThatThrownBy {
            roundService!!.deliverRegeneratingCommands(ANY_ROUND_ID)
        }
        verify(mockRoundRepository!!, never()).save(any())
    }

    @Test
    fun shouldSendRepairItemUseCommandsToRobotWhenDispatchingRegeneratingCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.MINING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.REPAIRITEMUSE))
            .thenReturn(
                getListOfBlockingCommands(CommandType.REPAIRITEMUSE)
            )

        // when
        roundService!!.deliverRegeneratingCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.REPAIRITEMUSE)
        verify(mockRobotCommandDispatcherClient!!).sendRepairItemUseCommands(any())
    }

    @Test
    fun shouldSendRegeneratingCommandsToRobotWhenDispatchingRegeneratingCommands() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.MINING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.REGENERATE))
            .thenReturn(
                getListOfBlockingCommands(CommandType.REGENERATE)
            )

        // when
        roundService!!.deliverRegeneratingCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findByGameIdAndRoundNumberAndCommandType(ANY_GAMEID, ANY_ROUND_NUMBER, CommandType.REGENERATE)
        verify(mockRobotCommandDispatcherClient!!).sendRegeneratingCommands(any())
    }




    private fun getListOfBlockingCommands(commandType: CommandType) = listOf(
        Command(
            gameId = ANY_GAMEID,
            roundNumber = ANY_ROUND_NUMBER,
            playerId = ANY_PLAYERID,
            robotId = ANY_ROBOTID,
            commandType = commandType,
            commandObject = CommandObject(
                commandType, UUID.randomUUID(), UUID.randomUUID(), "ANY_ITEMNAME", 1
            )
        )
    )
}