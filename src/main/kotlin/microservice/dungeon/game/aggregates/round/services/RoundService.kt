package microservice.dungeon.game.aggregates.round.services

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundNotFoundException
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStatusEvent
import microservice.dungeon.game.aggregates.round.events.RoundStatusEventBuilder
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
import microservice.dungeon.game.aggregates.round.web.TradingCommandDispatcherClient
import microservice.dungeon.game.aggregates.round.web.dto.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoundService @Autowired constructor (
    private val roundRepository: RoundRepository,
    private val commandRepository: CommandRepository,
    private val eventStoreService: EventStoreService,
    private val gameRepository: GameRepository,
    private val eventPublisherService: EventPublisherService,
    private val robotCommandDispatcherClient: RobotCommandDispatcherClient,
    private val tradingCommandDispatcherClient: TradingCommandDispatcherClient,
    private val roundStatusEventBuilder: RoundStatusEventBuilder
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun endCommandInputs(roundId: UUID) {
        val round: Round
        val transactionId = UUID.randomUUID()

        try {
            round = roundRepository.findById(roundId).get()
        } catch (e: Exception) {
            logger.error("Failed to end Command-Input-Phase. Round does not exist. [roundId={}]", roundId)
            logger.error(e.message)
            throw RoundNotFoundException("Failed to find round with roundId $roundId.")
        }

        round.endCommandInputPhase()
        roundRepository.save(round)

        val roundEvent: RoundStatusEvent = roundStatusEventBuilder.makeRoundStatusEvent(
            transactionId, round.getGameId(), round.getRoundId(), round.getRoundNumber(), RoundStatus.COMMAND_INPUT_ENDED
        )
        eventStoreService.storeEvent(roundEvent)
        eventPublisherService.publishEvent(roundEvent)
        logger.debug("RoundStatusEvent handed off to EventStore & -Publisher. [roundNumber={}, roundStatus={}]",
            round.getRoundNumber(), RoundStatus.COMMAND_INPUT_ENDED)

        logger.info("Command-Input-Phase ended in Round {}.", roundId)
    }
/*
    fun deliverBlockingCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()

        val commands: List<Command> = commandRepository.findAllCommandsByRoundAndCommandType(round, CommandType.BLOCKING)
        val commandDtos: List<BlockCommandDto> = Command.parseCommandsToDto(commands) {
            BlockCommandDto.makeFromCommand(it)
        }

        robotCommandDispatcherClient.sendBlockingCommands(commandDtos)
        round.deliverBlockingCommandsToRobot()
        roundRepository.save(round)
        logger.info("{} Blocking-Command(s) dispatched in Round {}.", commandDtos.size, round.getRoundNumber())
    }
*/
    fun deliverTradingCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()

        val sellingCommands: List<Command> = commandRepository.findAllCommandsByRoundAndCommandType(round, CommandType.SELLING)
        val sellingCommandDtos: List<SellCommandDto> = Command.parseCommandsToDto(sellingCommands) {
            SellCommandDto.makeFromCommand(it)
        }
        val buyingCommands: List<Command> = commandRepository.findAllCommandsByRoundAndCommandType(round, CommandType.BUYING)
        val buyingCommandDtos: List<BuyCommandDto> = Command.parseCommandsToDto(buyingCommands) {
            BuyCommandDto.makeFromCommand(it)
        }

        tradingCommandDispatcherClient.sendSellingCommands(sellingCommandDtos)
        tradingCommandDispatcherClient.sendBuyingCommands(buyingCommandDtos)
        round.deliverSellingCommandsToRobot()
        round.deliverBuyingCommandsToRobot()
        roundRepository.save(round)
        logger.info("{} Selling-Command(s) & {} Buying-Command(s) dispatched in Round {}.",
            sellingCommandDtos.size, buyingCommandDtos.size, round.getRoundNumber())
    }

    fun deliverMovementCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()

        //val itemMovementCommands: List<Command> = commandRepository.findAllCommandsByRoundAndCommandType(round, CommandType.MOVEITEMUSE)
      /*  val itemMovementCommandDtos: List<UseItemMovementCommandDto> = Command.parseCommandsToDto(itemMovementCommands) {
            UseItemMovementCommandDto.makeFromCommand(it)
        }
       */ val movementCommands: List<Command> = commandRepository.findAllCommandsByRoundAndCommandType(round, CommandType.MOVEMENT)
        val movementCommandDtos: List<MovementCommandDto> = Command.parseCommandsToDto(movementCommands) {
            MovementCommandDto.makeFromCommand(it)
        }

     //   robotCommandDispatcherClient.sendMovementItemUseCommands(itemMovementCommandDtos)
        robotCommandDispatcherClient.sendMovementCommands(movementCommandDtos)
      //  round.deliverMovementItemUseCommandsToRobot()
        round.deliverMovementCommandsToRobot()
        roundRepository.save(round)
     //   logger.info("{} Item-Movement-Command(s) & {} Movement-Command(s) dispatched in Round {}.",
      //      itemMovementCommandDtos.size, movementCommandDtos.size, round.getRoundNumber())
    }

    fun deliverBattleCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()

     /*   val itemFightCommands: List<Command> = commandRepository.findAllCommandsByRoundAndCommandType(round, CommandType.BATTLEITEMUSE)
        val itemFightCommandDtos: List<UseItemFightCommandDto> = Command.parseCommandsToDto(itemFightCommands) {
            UseItemFightCommandDto.makeFromCommand(it)
        }
     */   val fightCommands: List<Command> = commandRepository.findAllCommandsByRoundAndCommandType(round, CommandType.BATTLE)
        val fightCommandDtos: List<FightCommandDto> = Command.parseCommandsToDto(fightCommands) {
            FightCommandDto.makeFromCommand(it)
        }

//        robotCommandDispatcherClient.sendBattleItemUseCommands(itemFightCommandDtos)
        robotCommandDispatcherClient.sendBattleCommands(fightCommandDtos)
      //  round.deliverBattleItemUseCommandsToRobot()
        round.deliverBattleCommandsToRobot()
        roundRepository.save(round)
     //   logger.info("{} Item-Battle-Command(s) & {} Battle-Command(s) dispatched in Round {}.",
     //       itemFightCommandDtos.size, fightCommandDtos.size, round.getRoundNumber())
    }

    fun deliverMiningCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()

        val miningCommands: List<Command> = commandRepository.findAllCommandsByRoundAndCommandType(round, CommandType.MINING)
        val miningCommandDtos: List<MineCommandDto> = Command.parseCommandsToDto(miningCommands) {
            MineCommandDto.makeFromCommand(it)
        }

        robotCommandDispatcherClient.sendMiningCommands(miningCommandDtos)
        round.deliverMiningCommandsToRobot()
        roundRepository.save(round)
        logger.info("{} Mining-Commands dispatched in Round {}.", miningCommandDtos.size, round.getRoundNumber())
    }

    fun deliverRegeneratingCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()

      /*  val itemRepairCommands: List<Command> = commandRepository.findAllCommandsByRoundAndCommandType(round, CommandType.REPAIRITEMUSE)
        val itemRepairCommandDtos: List<UseItemRepairCommandDto> = Command.parseCommandsToDto(itemRepairCommands) {
            UseItemRepairCommandDto.makeFromCommand(it)
        }
      */  val regenCommands: List<Command> = commandRepository.findAllCommandsByRoundAndCommandType(round, CommandType.REGENERATE)
        val regenCommandDtos: List<RegenerateCommandDto> = Command.parseCommandsToDto(regenCommands) {
            RegenerateCommandDto.makeFromCommands(it)
        }

      //  robotCommandDispatcherClient.sendRepairItemUseCommands(itemRepairCommandDtos)
        robotCommandDispatcherClient.sendRegeneratingCommands(regenCommandDtos)
       // round.deliverRepairItemUseCommandsToRobot()
        round.deliverRegeneratingCommandsToRobot()
        roundRepository.save(round)
      //  logger.info("{} Repair-Command(s) & {} Regeneration-Command(s) dispatched in Round {}.",
    //        itemRepairCommandDtos.size, regenCommandDtos.size, round.getRoundNumber())
    }

    fun endRound(roundId: UUID) {
        val round: Round
        val transactionId = UUID.randomUUID()

        try {
            round = roundRepository.findById(roundId).get()

        } catch (e: Exception){
            logger.error("Failed to end round. Round does not exist. [roundId=$roundId]")
            logger.error(e.message)
            throw RoundNotFoundException("Failed to find round with roundId $roundId.")
        }

        val response: Boolean = round.endRound()
        roundRepository.save(round)

        if (response) {
            val roundEvent: RoundStatusEvent = roundStatusEventBuilder.makeRoundStatusEvent(
                transactionId, round.getGameId(), round.getRoundId(), round.getRoundNumber(), RoundStatus.ROUND_ENDED
            )
            eventStoreService.storeEvent(roundEvent)
            eventPublisherService.publishEvent(roundEvent)
            logger.debug("RoundStatusEvent handed off to EventStore & -Publisher. [roundNumber=${round.getRoundNumber()}, roundStatus=ROUND_ENDED]")
        }

        logger.info("Round {} ended.", round.getRoundNumber())
    }
}