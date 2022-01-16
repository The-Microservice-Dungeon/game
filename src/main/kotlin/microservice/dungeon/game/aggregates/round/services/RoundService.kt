package microservice.dungeon.game.aggregates.round.services

import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.dtos.*
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
import microservice.dungeon.game.aggregates.round.web.TradingCommandDispatcherClient
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
    private val tradingCommandDispatcherClient: TradingCommandDispatcherClient
) {
    private val logger = KotlinLogging.logger {}


    fun endCommandInputs(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.endCommandInputPhase()
        roundRepository.save(round)
//        val commandInputEnded = CommandInputEnded(round)
//        eventStoreService.storeEvent(commandInputEnded)
//        eventPublisherService.publishEvents(listOf(commandInputEnded))
    }



    fun deliverBlockingCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        robotCommandDispatcherClient.sendBlockingCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.BLOCKING
            )
            .map { command -> BlockCommandDTO.fromCommand(command) }
        )
        round.deliverBlockingCommandsToRobot()
        roundRepository.save(round)
        logger.info("Blocking-Commands dispatched.")
    }


    fun deliverTradingCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverSellingCommandsToRobot()
        tradingCommandDispatcherClient.sendSellingCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.SELLING
            )
            .map { command -> SellCommandDTO.fromCommand (command) }
        )
        round.deliverBuyingCommandsToRobot()
        tradingCommandDispatcherClient.sendBuyingCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.BUYING
            )
                .map { command -> BuyCommandDTO.fromCommand (command) }
        )
        roundRepository.save(round)
        logger.info("Trading-Commands dispatched.")
    }


    fun deliverMovementCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverMovementItemUseCommandsToRobot()
        robotCommandDispatcherClient.sendMovementItemUseCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.MOVEITEMUSE
            )
            .map { command -> UseItemMovementCommandDTO.fromCommand(command) }
        )
        round.deliverMovementCommandsToRobot()
        robotCommandDispatcherClient.sendMovementCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.MOVEMENT
            )
            .map { command -> MovementCommandDTO.fromCommand(command) }
        )
        roundRepository.save(round)
        logger.info("Movement-Commands dispatched.")
    }


    fun deliverBattleCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverBattleItemUseCommandsToRobot()
        robotCommandDispatcherClient.sendBattleItemUseCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.BATTLEITEMUSE
            )
            .map { command -> UseItemFightCommandDTO.fromCommand(command) }
        )
        round.deliverBattleCommandsToRobot()
        robotCommandDispatcherClient.sendBattleCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.BATTLE
            )
                .map { command -> FightCommandDTO.fromCommand(command) }
        )
        roundRepository.save(round)
        logger.info("Battle-Commands dispatched.")
    }


    fun deliverMiningCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverMiningCommandsToRobot()
        robotCommandDispatcherClient.sendMiningCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.MINING
            )
                .map { command -> MineCommandDTO.fromCommand(command) }
        )
        roundRepository.save(round)
        logger.info("Mining-Commands dispatched.")
    }


    fun deliverRegeneratingCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverRepairItemUseCommandsToRobot()
        robotCommandDispatcherClient.sendRepairItemUseCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.REPAIRITEMUSE
            )
                .map { command -> UseItemRepairCommandDTO.fromCommand(command) }
        )
        round.deliverRegeneratingCommandsToRobot()
        robotCommandDispatcherClient.sendRegeneratingCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.REGENERATE
            )
                .map { command -> RegenerateCommandDTO.fromCommand(command) }
        )
        roundRepository.save(round)
        logger.info("Regeneration-Commands dispatched.")
    }


    fun endRound(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        val response = round.endRound()
        roundRepository.save(round)
        if (response) {
//            val roundEnded = RoundEnded(round)
//            eventStoreService.storeEvent(roundEnded)
//            eventPublisherService.publishEvents(listOf(roundEnded))
        }
    }
}