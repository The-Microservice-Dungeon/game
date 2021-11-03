package microservice.dungeon.game.aggregates.round.services

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RoundService @Autowired constructor (
    private val roundRepository: RoundRepository
) {
    fun startNewRound(roundNumber: Int) {
        if (!roundRepository.findByRoundNumber(roundNumber).isEmpty) {
            throw EntityAlreadyExistsException("Round $roundNumber already exists.")
        }
        val round = Round(roundNumber)
        roundRepository.save(round)
    }

    fun endCommandInputs(roundNumber: Int) {
        val round = roundRepository.findByRoundNumber(roundNumber).get()
        round.endCommandInputPhase()
        roundRepository.save(round)
    }

    fun deliverBlockingCommands(roundNumber: Int) {
        val round = roundRepository.findByRoundNumber(roundNumber).get()
        // TODO
        roundRepository.save(round)
    }

    fun deliverTradingCommands(roundNumber: Int) {
        val round = roundRepository.findByRoundNumber(roundNumber).get()
        // TODO
        roundRepository.save(round)
    }

    fun deliverMovementCommands(roundNumber: Int) {
        val round = roundRepository.findByRoundNumber(roundNumber).get()
        // TODO
        roundRepository.save(round)
    }

    fun deliverBattleCommands(roundNumber: Int) {
        val round = roundRepository.findByRoundNumber(roundNumber).get()
        // TODO
        roundRepository.save(round)
    }

    fun deliverMiningCommands(roundNumber: Int) {
        val round = roundRepository.findByRoundNumber(roundNumber).get()
        // TODO
        roundRepository.save(round)
    }

    fun deliverScoutingCommands(roundNumber: Int) {
        val round = roundRepository.findByRoundNumber(roundNumber).get()
        // TODO
        roundRepository.save(round)
    }

    fun endRound(roundNumber: Int) {
        val round = roundRepository.findByRoundNumber(roundNumber).get()
        round.endRound()
        roundRepository.save(round)
    }
}

