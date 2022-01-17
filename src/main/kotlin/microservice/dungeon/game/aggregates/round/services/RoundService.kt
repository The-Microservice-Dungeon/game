package microservice.dungeon.game.aggregates.round.services

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
            logger.error("Failed to end Command-Input-Phase. Round does not exist. [roundId=$roundId]")
            logger.error(e.message)
            throw RoundNotFoundException("Failed to find round with roundId $roundId.")
        }

        round.endCommandInputPhase()
        roundRepository.save(round)

        val roundEvent: RoundStatusEvent = roundStatusEventBuilder.makeRoundStatusEvent(
            transactionId, round.getRoundId(), round.getRoundNumber(), RoundStatus.COMMAND_INPUT_ENDED
        )
        eventStoreService.storeEvent(roundEvent)
        eventPublisherService.publishEvent(roundEvent)
        logger.debug("RoundStatusEvent handed off to EventStore & -Publisher. [roundNumber=${round.getRoundNumber()}, roundStatus=${RoundStatus.COMMAND_INPUT_ENDED}]")

        logger.info("Command-Input-Phase ended. [roundNumber=$roundId]")
    }

    fun deliverBlockingCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        robotCommandDispatcherClient.sendBlockingCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.BLOCKING
            )
            .map { command -> BlockCommandDto.makeFromCommand(command) }
        )

        round.deliverBlockingCommandsToRobot()
        roundRepository.save(round)
        logger.info("Blocking-Commands dispatched. [roundNumber=${round.getRoundNumber()}]")
    }

    fun deliverTradingCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()

        tradingCommandDispatcherClient.sendSellingCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.SELLING
            )
            .map { command -> SellCommandDto.makeFromCommand (command) }
        )
        round.deliverSellingCommandsToRobot()
        tradingCommandDispatcherClient.sendBuyingCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.BUYING
            )
                .map { command -> BuyCommandDto.makeFromCommand (command) }
        )
        round.deliverBuyingCommandsToRobot()
        roundRepository.save(round)
        logger.info("Trading-Commands dispatched. [roundNumber=${round.getRoundNumber()}]")
    }

    fun deliverMovementCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()

        robotCommandDispatcherClient.sendMovementItemUseCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.MOVEITEMUSE
            )
            .map { command -> UseItemMovementCommandDto.makeFromCommand(command) }
        )
        round.deliverMovementItemUseCommandsToRobot()
        robotCommandDispatcherClient.sendMovementCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.MOVEMENT
            )
            .map { command -> MovementCommandDto.makeFromCommand(command) }
        )
        round.deliverMovementCommandsToRobot()
        roundRepository.save(round)
        logger.info("Movement-Commands dispatched. [roundNumber=${round.getRoundNumber()}]")
    }

    fun deliverBattleCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()

        robotCommandDispatcherClient.sendBattleItemUseCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.BATTLEITEMUSE
            )
            .map { command -> UseItemFightCommandDto.makeFromCommand(command) }
        )
        round.deliverBattleItemUseCommandsToRobot()
        robotCommandDispatcherClient.sendBattleCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.BATTLE
            )
                .map { command -> FightCommandDto.makeFromCommand(command) }
        )
        round.deliverBattleCommandsToRobot()
        roundRepository.save(round)
        logger.info("Battle-Commands dispatched. [roundNumber=${round.getRoundNumber()}]")
    }

    fun deliverMiningCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()

        robotCommandDispatcherClient.sendMiningCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.MINING
            )
                .map { command -> MineCommandDto.makeFromCommand(command) }
        )
        round.deliverMiningCommandsToRobot()
        roundRepository.save(round)
        logger.info("Mining-Commands dispatched. [roundNumber=${round.getRoundNumber()}]")
    }

    fun deliverRegeneratingCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()

        robotCommandDispatcherClient.sendRepairItemUseCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.REPAIRITEMUSE
            )
                .map { command -> UseItemRepairCommandDto.makeFromCommand(command) }
        )
        round.deliverRepairItemUseCommandsToRobot()
        robotCommandDispatcherClient.sendRegeneratingCommands(
            commandRepository.findByGameIdAndRoundNumberAndCommandType(
                round.getGameId(), round.getRoundNumber(), CommandType.REGENERATE
            )
                .map { command -> RegenerateCommandDto.makeFromCommands(command) }
        )
        round.deliverRegeneratingCommandsToRobot()
        roundRepository.save(round)
        logger.info("Regeneration-Commands dispatched. [roundNumber=${round.getRoundNumber()}]")
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
                transactionId, round.getRoundId(), round.getRoundNumber(), RoundStatus.ROUND_ENDED
            )
            eventStoreService.storeEvent(roundEvent)
            eventPublisherService.publishEvent(roundEvent)
            logger.debug("RoundStatusEvent handed off to EventStore & -Publisher. [roundNumber=${round.getRoundNumber()}, roundStatus=ROUND_ENDED]")
        }

        logger.info("Round ended. [roundNumber=${round.getRoundNumber()}]")
    }
}