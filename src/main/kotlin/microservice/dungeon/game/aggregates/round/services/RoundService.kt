package microservice.dungeon.game.aggregates.round.services

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RoundService @Autowired constructor (
    private val roundRepository: RoundRepository,
    private val eventStoreService: EventStoreService,
    private val eventPublisherService: EventPublisherService
) {
    @Transactional
    fun startNewRound(roundNumber: Int) {
        if (!roundRepository.findByRoundNumber(roundNumber).isEmpty) {
            throw EntityAlreadyExistsException("A round with number $roundNumber already exists.")
        }
        val round = Round(roundNumber)
        roundRepository.save(round)
        val roundStarted: RoundStarted = RoundStarted(LocalDateTime.now(), round.roundNumber, RoundStatus.COMMAND_INPUT_STARTED)
        eventStoreService.storeEvent(roundStarted)
        eventPublisherService.publishEvents(listOf(roundStarted))
    }

    fun endCommandInputs(roundNumber: Int) {
        //TODO
    }

    fun deliverBlockingCommands(roundNumber: Int) {
        //TODO
    }

    fun deliverTradingCommands(roundNumber: Int) {
        //TODO
    }

    fun deliverMovementCommands(roundNumber: Int) {
        //TODO
    }

    fun deliverBattleCommands(roundNumber: Int) {
        //TODO
    }

    fun deliverMiningCommands(roundNumber: Int) {
        //TODO
    }

    fun deliverScoutingCommands(roundNumber: Int) {
        //TODO
    }

    @Transactional
    fun endRound(roundNumber: Int) {

    }
}