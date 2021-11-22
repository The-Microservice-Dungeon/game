package microservice.dungeon.game.aggregates.round.services

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.web.CommandDispatcherClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class RoundService @Autowired constructor (
    private val roundRepository: RoundRepository,
    private val eventStoreService: EventStoreService,
    private val eventPublisherService: EventPublisherService
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
    //TODO("Commands + Dispatcher")
    fun deliverBlockingCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverBlockingCommandsToRobot()
        roundRepository.save(round)
//        commandDispatcherClient.dispatchBlockingCommands(round.getRoundNumber(), emptyList())
    }

    @Transactional
    //TODO("Commands + Dispatcher")
    fun deliverTradingCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverTradingCommandsToRobot()
        roundRepository.save(round)
//        commandDispatcherClient.dispatchTradingCommands(round.getRoundNumber(), emptyList())
    }

    @Transactional
    //TODO("Commands + Dispatcher")
    fun deliverMovementCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverMovementCommandsToRobot()
        roundRepository.save(round)
//        commandDispatcherClient.dispatchMovementCommands(round.getRoundNumber(), emptyList())
    }

    @Transactional
    //TODO("Commands + Dispatcher")
    fun deliverBattleCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverBattleCommandsToRobot()
        roundRepository.save(round)
//        commandDispatcherClient.dispatchBattleCommands(round.getRoundNumber(), emptyList())
    }

    @Transactional
    //TODO("Commands + Dispatcher")
    fun deliverMiningCommands(roundId: UUID) {
        val round: Round = roundRepository.findById(roundId).get()
        round.deliverMiningCommandsToRobot()
        roundRepository.save(round)
//        commandDispatcherClient.dispatchMiningCommands(round.getRoundNumber(), emptyList())
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