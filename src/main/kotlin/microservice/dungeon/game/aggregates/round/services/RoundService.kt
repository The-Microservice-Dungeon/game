package microservice.dungeon.game.aggregates.round.services

import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.dtos.BlockCommandDTO
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
    private val robotCommandDispatcherClient: RobotCommandDispatcherClient
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
        round.deliverBuyingCommandsToRobot()
        roundRepository.save(round)
    }

    @Transactional
    fun deliverMovementCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverMovementItemUseCommandsToRobot()
        round.deliverMovementCommandsToRobot()
        roundRepository.save(round)
    }

    @Transactional
    fun deliverBattleCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverBattleItemUseCommandsToRobot()
        round.deliverBattleCommandsToRobot()
        roundRepository.save(round)
    }

    @Transactional
    fun deliverMiningCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverMiningCommandsToRobot()
        roundRepository.save(round)
    }

    @Transactional
    fun deliverRegeneratingCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverRepairItemUseCommandsToRobot()
        round.deliverRegeneratingCommandsToRobot()
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