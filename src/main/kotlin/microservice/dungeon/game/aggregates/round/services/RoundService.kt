package microservice.dungeon.game.aggregates.round.services

import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RoundService @Autowired constructor (
    private val roundRepository: RoundRepository
) {
    fun startNewRound(roundNumber: Int) {
        // TODO(check if round already exists and throw)
        val round = Round(roundNumber)
        roundRepository.save(round)
    }

    fun endCommandInputs(roundNumber: Int) {

    }

    fun deliverBlockingCommands(roundNumber: Int) {

    }

    fun deliverTradingCommands(roundNumber: Int) {

    }

    fun deliverMovementCommands(roundNumber: Int) {

    }

    fun deliverBattleCommands(roundNumber: Int) {

    }

    fun deliverMiningCommands(roundNumber: Int) {

    }

    fun deliverScoutingCommands(roundNumber: Int) {

    }

    fun endRound(roundNumber: Int) {

    }
}

// TODO(PERSISTENCE)
// TODO(COMMAND SERVICE)
// TODO(REST CLIENT)
// TODO(EVENT PUBLISHER)
// TODO(LOCALDATETIME OR DATE OR TIME?)