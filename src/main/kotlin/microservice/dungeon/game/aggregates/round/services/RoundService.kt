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

@Service
class RoundService @Autowired constructor (
    private val roundRepository: RoundRepository,
    private val eventStoreService: EventStoreService,
    private val eventPublisherService: EventPublisherService,
    private val commandDispatcherClient: CommandDispatcherClient
) {
    @Transactional
    fun startNewRound(roundNumber: Int) {
        if (!roundRepository.findByRoundNumber(roundNumber).isEmpty) {
            throw EntityAlreadyExistsException("A round with number $roundNumber already exists.")
        }
        val round = Round(roundNumber)
        roundRepository.save(round)
        val roundStarted = RoundStarted(LocalDateTime.now(), roundNumber, RoundStatus.COMMAND_INPUT_STARTED)
        eventStoreService.storeEvent(roundStarted)
        eventPublisherService.publishEvents(listOf(roundStarted))
    }

    @Transactional
    fun endCommandInputs(roundNumber: Int) {
        val round: Round = roundRepository.findByRoundNumber(roundNumber).get()
        round.endCommandInputPhase()
        roundRepository.save(round)
        val commandInputEnded = CommandInputEnded(LocalDateTime.now(), roundNumber, RoundStatus.COMMAND_INPUT_ENDED)
        eventStoreService.storeEvent(commandInputEnded)
        eventPublisherService.publishEvents(listOf(commandInputEnded))
    }


    @Transactional
    //TODO("Commands")
    fun deliverBlockingCommands(roundNumber: Int) {
        val round: Round = roundRepository.findByRoundNumber(roundNumber).get()
        round.deliverBlockingCommandsToRobot()
        roundRepository.save(round)
        commandDispatcherClient.dispatchBlockingCommands(roundNumber, emptyList())
    }

    @Transactional
    //TODO("Commands")
    fun deliverTradingCommands(roundNumber: Int) {
        val round: Round = roundRepository.findByRoundNumber(roundNumber).get()
        round.deliverTradingCommandsToRobot()
        roundRepository.save(round)
        commandDispatcherClient.dispatchTradingCommands(roundNumber, emptyList())
    }

    @Transactional
    //TODO("Commands")
    fun deliverMovementCommands(roundNumber: Int) {
        val round: Round = roundRepository.findByRoundNumber(roundNumber).get()
        round.deliverMovementCommandsToRobot()
        roundRepository.save(round)
        commandDispatcherClient.dispatchMovementCommands(roundNumber, emptyList())
    }

    @Transactional
    //TODO("Commands")
    fun deliverBattleCommands(roundNumber: Int) {
        val round: Round = roundRepository.findByRoundNumber(roundNumber).get()
        round.deliverBattleCommandsToRobot()
        roundRepository.save(round)
        commandDispatcherClient.dispatchBattleCommands(roundNumber, emptyList())
    }

    @Transactional
    //TODO("Commands")
    fun deliverMiningCommands(roundNumber: Int) {
        val round: Round = roundRepository.findByRoundNumber(roundNumber).get()
        round.deliverMiningCommandsToRobot()
        roundRepository.save(round)
        commandDispatcherClient.dispatchMiningCommands(roundNumber, emptyList())
    }

    @Transactional
    //TODO("Commands")
    fun deliverScoutingCommands(roundNumber: Int) {
        val round: Round = roundRepository.findByRoundNumber(roundNumber).get()
        round.deliverScoutingCommandsToRobot()
        roundRepository.save(round)
        commandDispatcherClient.dispatchScoutingCommands(roundNumber, emptyList())
    }

    @Transactional
    fun endRound(roundNumber: Int) {
        val round: Round = roundRepository.findByRoundNumber(roundNumber).get()
        round.endRound()
        roundRepository.save(round)
        val roundEnded = RoundEnded(LocalDateTime.now(), roundNumber, RoundStatus.ROUND_ENDED)
        eventStoreService.storeEvent(roundEnded)
        eventPublisherService.publishEvents(listOf(roundEnded))
    }
}