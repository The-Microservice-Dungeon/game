package microservice.dungeon.game.aggregates.round.services

import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.dtos.*
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
import microservice.dungeon.game.aggregates.round.web.TradingCommandDispatcherClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class RoundService @Autowired constructor (
    private val roundRepository: RoundRepository,
    private val commandRepository: CommandRepository,
    private val eventStoreService: EventStoreService,
    private val eventPublisherService: EventPublisherService,
    private val robotCommandDispatcherClient: RobotCommandDispatcherClient,
    private val tradingCommandDispatcherClient: TradingCommandDispatcherClient
) {
    @Transactional
    fun startNewRound(gameId: UUID, roundNumber: Int): UUID {
        if (!roundRepository.findByGameIdAndRoundNumber(gameId, roundNumber).isEmpty) {
            throw EntityAlreadyExistsException("A round with number $roundNumber for game $gameId already exists.")
        }
        val round = Round(gameId, roundNumber)
        roundRepository.save(round)
        val roundStarted = RoundStarted(round)
        eventStoreService.storeEvent(roundStarted)
        eventPublisherService.publishEvents(listOf(roundStarted))
        return round.getRoundId()
    }

    @Transactional
    fun endCommandInputs(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.endCommandInputPhase()
        roundRepository.save(round)
        val commandInputEnded = CommandInputEnded(round)
        eventStoreService.storeEvent(commandInputEnded)
        eventPublisherService.publishEvents(listOf(commandInputEnded))
    }


    @Transactional
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
    }

    @Transactional
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
        roundRepository.save(round)
    }

    @Transactional
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
    }

    @Transactional
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
    }

    @Transactional
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
    }

    @Transactional
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
    }

    @Transactional
    fun endRound(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        val response = round.endRound()
        roundRepository.save(round)
        if (response) {
            val roundEnded = RoundEnded(round)
            eventStoreService.storeEvent(roundEnded)
            eventPublisherService.publishEvents(listOf(roundEnded))
        }
    }
}