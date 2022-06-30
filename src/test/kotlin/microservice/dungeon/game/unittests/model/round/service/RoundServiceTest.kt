package microservice.dungeon.game.unittests.model.round.service

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandPayload
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStatusEvent
import microservice.dungeon.game.aggregates.round.events.RoundStatusEventBuilder
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
import microservice.dungeon.game.aggregates.round.web.TradingCommandDispatcherClient
import microservice.dungeon.game.aggregates.round.web.dto.BlockCommandDto
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
    private var mockTradingCommandDispatcherCLient: TradingCommandDispatcherClient? = null
    private var mockGameRepository: GameRepository? = null
    private var mockRoundRepository: RoundRepository? = null
    private var mockCommandRepository: CommandRepository? = null
    private var roundService: RoundService? = null

    private val roundStatusEventBuilder = RoundStatusEventBuilder("anyTopic", "anyType", 1)

    private val GAME = Game(10, 100)
    private val ANY_ROUND_ID = UUID.randomUUID()
    private val ANY_GAMEID = GAME.getGameId()
    private val ANY_PLAYERID = UUID.randomUUID()
    private val ANY_ROBOTID = UUID.randomUUID()
    private val ANY_ROUND_NUMBER = 3


    @BeforeEach
    fun setUp() {
        mockEventStoreService = mock()
        mockEventPublisherService = mock()
        mockRobotCommandDispatcherClient = mock()
        mockTradingCommandDispatcherCLient = mock()
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
            mockTradingCommandDispatcherCLient!!,
            roundStatusEventBuilder
        )
    }



    // End Command Inputs

    @Test
    fun shouldAllowEndCommandInput() {
        // given
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.COMMAND_INPUT_STARTED))
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
        // given
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.COMMAND_INPUT_STARTED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.endCommandInputs(ANY_GAMEID)

        // then
        verify(mockEventStoreService!!).storeEvent(check { event: RoundStatusEvent ->
            assertThat(event.roundId)
                .isEqualTo(spyRound.getRoundId())
            assertThat(event.roundNumber)
                .isEqualTo(spyRound.getRoundNumber())
            assertThat(event.roundStatus)
                .isEqualTo(RoundStatus.COMMAND_INPUT_ENDED)
        })
        verify(mockEventPublisherService!!).publishEvent(check { event: RoundStatusEvent ->
            assertThat(event.roundId)
                .isEqualTo(spyRound.getRoundId())
            assertThat(event.roundNumber)
                .isEqualTo(spyRound.getRoundNumber())
            assertThat(event.roundStatus)
                .isEqualTo(RoundStatus.COMMAND_INPUT_ENDED)
        })
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
        val commandInputAlreadyEndedRound = Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.COMMAND_INPUT_ENDED)
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
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.COMMAND_INPUT_ENDED))
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
        val commands: List<Command> = getListOfCommands(CommandType.BLOCKING)
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.COMMAND_INPUT_ENDED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findAllCommandsByRoundAndCommandType(spyRound, CommandType.BLOCKING))
            .thenReturn(commands)

        // when
        roundService!!.deliverBlockingCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findAllCommandsByRoundAndCommandType(spyRound, CommandType.BLOCKING)
        verify(mockRobotCommandDispatcherClient!!).sendBlockingCommands(any())
    }

    @Test
    fun shouldIgnoreConversionErrorsFromCommandToDtoWhenDispatchingBlockingCommands() {
        // given
        val validCommand = getValidCommand(CommandType.BLOCKING)
        val invalidCommand = getInvalidCommand(CommandType.BLOCKING)
        val round = Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.COMMAND_INPUT_ENDED)

        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(round))
        whenever(mockCommandRepository!!.findAllCommandsByRoundAndCommandType(round, CommandType.BLOCKING))
            .thenReturn(listOf(validCommand, invalidCommand))

        // when
        roundService!!.deliverBlockingCommands(ANY_GAMEID)

        // then
        verify(mockRobotCommandDispatcherClient!!).sendBlockingCommands(check { commands: List<BlockCommandDto> ->
            assertThat(commands)
                .hasSize(1)
        })
    }





    // Dispatch TRADING Commands

    @Test
    fun shouldAllowToDispatchTradingCommands() {
        // given
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.BLOCKING_COMMANDS_DISPATCHED))
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
        // given
        val commands: List<Command> = getListOfCommands(CommandType.SELLING)
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.BLOCKING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findAllCommandsByRoundAndCommandType(spyRound, CommandType.SELLING))
            .thenReturn(commands)

        // when
        roundService!!.deliverTradingCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findAllCommandsByRoundAndCommandType(spyRound, CommandType.SELLING)
        verify(mockTradingCommandDispatcherCLient!!).sendSellingCommands(any())
    }

    @Test
    fun shouldSendBuyingCommandsToRobotWhenDispatchingTradingCommandsAfterSendingSellingCommands() {
        // given
        val commands: List<Command> = getListOfCommands(CommandType.BUYING)
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.BLOCKING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findAllCommandsByRoundAndCommandType(spyRound, CommandType.BUYING))
            .thenReturn(commands)

        // when
        roundService!!.deliverTradingCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findAllCommandsByRoundAndCommandType(spyRound, CommandType.BUYING)
        verify(mockTradingCommandDispatcherCLient!!).sendSellingCommands(any())
        verify(mockTradingCommandDispatcherCLient!!).sendBuyingCommands(any())
    }





    // Dispatch MOVEMENT Commands

    @Test
    fun shouldAllowToDispatchMovementCommands() {
        // given
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.BUYING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.deliverMovementCommands(ANY_GAMEID)

        // then
      //  verify(spyRound).deliverMovementItemUseCommandsToRobot()
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

   /* @Test
    fun shouldSendMovementItemUseCommandsToRobotWhenDispatchingMovementCommands() {
        // given
      /  val commands: List<Command> = getListOfCommands(CommandType.MOVEITEMUSE)
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.BUYING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findAllCommandsByRoundAndCommandType(spyRound, CommandType.MOVEITEMUSE))
            .thenReturn(commands)

        // when
        roundService!!.deliverMovementCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findAllCommandsByRoundAndCommandType(spyRound, CommandType.MOVEITEMUSE)
        verify(mockRobotCommandDispatcherClient!!).sendMovementItemUseCommands(any())
    }

    @Test
    fun shouldSendMovementCommandsToRobotWhenDispatchingMovementCommandsAfterSendingMovementItemUseCommands() {
        // given
        val commands: List<Command> = getListOfCommands(CommandType.MOVEMENT)
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.BUYING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findAllCommandsByRoundAndCommandType(spyRound, CommandType.MOVEMENT))
            .thenReturn(commands)

        // when
        roundService!!.deliverMovementCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findAllCommandsByRoundAndCommandType(spyRound, CommandType.MOVEMENT)
        verify(mockRobotCommandDispatcherClient!!).sendMovementItemUseCommands(any())
        verify(mockRobotCommandDispatcherClient!!).sendMovementCommands(any())
    }





    // Dispatch BATTLE Commands

    @Test
    fun shouldAllowToDispatchBattleCommands() {
        // given
        val spyRound = spy(Round(game = GAME, roundNumber =ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.MOVEMENT_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.deliverBattleCommands(ANY_GAMEID)

        // then
        verify(spyRound).deliverBattleItemUseCommandsToRobot()
        verify(spyRound).deliverBattleCommandsToRobot()
        verify(mockRoundRepository!!).save(isA<Round>())
    }
*/
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
/*
    @Test
    fun shouldSendBattleItemUseCommandsToRobotWhenDispatchingBattleCommands() {
        // given
        val commands: List<Command> = getListOfCommands(CommandType.BATTLEITEMUSE)
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.MOVEMENT_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findAllCommandsByRoundAndCommandType(spyRound, CommandType.BATTLEITEMUSE))
            .thenReturn(commands)

        // when
        roundService!!.deliverBattleCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findAllCommandsByRoundAndCommandType(spyRound, CommandType.BATTLEITEMUSE)
        verify(mockRobotCommandDispatcherClient!!).sendBattleItemUseCommands(any())
    }
*/
    @Test
    fun shouldSendBattleCommandsToRobotWhenDispatchingBattleCommandsAfterSendingBattleItemUseCommands() {
        // given
        val commands: List<Command> = getListOfCommands(CommandType.BATTLE)
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.MOVEMENT_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findAllCommandsByRoundAndCommandType(spyRound, CommandType.BATTLE))
            .thenReturn(commands)

        // when
        roundService!!.deliverBattleCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findAllCommandsByRoundAndCommandType(spyRound, CommandType.BATTLE)
        verify(mockRobotCommandDispatcherClient!!).sendBattleItemUseCommands(any())
        verify(mockRobotCommandDispatcherClient!!).sendBattleCommands(any())
    }





    // Dispatch MINING Commands

    @Test
    fun shouldAllowDispatchMiningCommands() {
        // given
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.BATTLE_COMMANDS_DISPATCHED))
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
        val commands: List<Command> = getListOfCommands(CommandType.MINING)
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.BATTLE_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findAllCommandsByRoundAndCommandType(spyRound, CommandType.MINING))
            .thenReturn(commands)

        // when
        roundService!!.deliverMiningCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findAllCommandsByRoundAndCommandType(spyRound, CommandType.MINING)
        verify(mockRobotCommandDispatcherClient!!).sendMiningCommands(any())
    }





    // Dispatch REGENERATING Commands

    @Test
    fun shouldAllowToDispatchRegeneratingCommands() {
        // given
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.MINING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.deliverRegeneratingCommands(ANY_GAMEID)

        // then
     //   verify(spyRound).deliverRepairItemUseCommandsToRobot()
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

  /*  @Test
    fun shouldSendRepairItemUseCommandsToRobotWhenDispatchingRegeneratingCommands() {
        // given
        val commands: List<Command> = getListOfCommands(CommandType.REPAIRITEMUSE)
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.MINING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findAllCommandsByRoundAndCommandType(spyRound, CommandType.REPAIRITEMUSE))
            .thenReturn(commands)

        // when
        roundService!!.deliverRegeneratingCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findAllCommandsByRoundAndCommandType(spyRound, CommandType.REPAIRITEMUSE)
        verify(mockRobotCommandDispatcherClient!!).sendRepairItemUseCommands(any())
    }
*/
    @Test
    fun shouldSendRegeneratingCommandsToRobotWhenDispatchingRegeneratingCommandsAfterSendingRepairItemUseCommands() {
        // given
        val commands: List<Command> = getListOfCommands(CommandType.REGENERATE)
        val spyRound = spy(Round(game = GAME, roundNumber = ANY_ROUND_NUMBER, roundId = ANY_ROUND_ID, roundStatus = RoundStatus.MINING_COMMANDS_DISPATCHED))
        whenever(mockRoundRepository!!.findById(ANY_ROUND_ID))
            .thenReturn(Optional.of(spyRound))
        whenever(mockCommandRepository!!.findAllCommandsByRoundAndCommandType(spyRound, CommandType.REGENERATE))
            .thenReturn(commands)

        // when
        roundService!!.deliverRegeneratingCommands(ANY_ROUND_ID)

        // then
        verify(mockCommandRepository!!).findAllCommandsByRoundAndCommandType(spyRound, CommandType.REGENERATE)
        verify(mockRobotCommandDispatcherClient!!).sendRepairItemUseCommands(any())
        verify(mockRobotCommandDispatcherClient!!).sendRegeneratingCommands(any())
    }




    private fun getListOfCommands(commandType: CommandType): List<Command> = listOf(getValidCommand(commandType))

    private fun getValidCommand(commandType: CommandType): Command {
        val round: Round = mock()
        whenever(round.getRoundNumber()).thenReturn(ANY_ROUND_NUMBER)
        whenever(round.getGameId()).thenReturn(ANY_GAMEID)

        val player: Player = mock()
        whenever(player.getPlayerId()).thenReturn(ANY_PLAYERID)

        val robot: Robot = mock()
        whenever(robot.getRobotId()).thenReturn(ANY_ROBOTID)

        return Command(
            round = round,
            player = player,
            robot = robot,
            commandType = commandType,
            commandPayload = CommandPayload(
                planetId = UUID.randomUUID(),
                targetId = UUID.randomUUID(),
                itemName = "any name",
                itemQuantity = 1
            )
        )
    }

    private fun getInvalidCommand(commandType: CommandType): Command {
        val round: Round = mock()
        whenever(round.getRoundNumber()).thenReturn(ANY_ROUND_NUMBER)
        whenever(round.getGameId()).thenReturn(ANY_GAMEID)

        val player: Player = mock()
        whenever(player.getPlayerId()).thenReturn(ANY_PLAYERID)

        return Command(
            round = round,
            player = player,
            robot = null,
            commandType = commandType,
            commandPayload = CommandPayload(null, null, null, null)
        )
    }
}