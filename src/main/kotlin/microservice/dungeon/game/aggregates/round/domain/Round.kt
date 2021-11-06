package microservice.dungeon.game.aggregates.round.domain

import lombok.Getter
import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import kotlin.math.round

@Entity
@Table(name = "rounds")
class Round(
    @Id
    private val roundNumber: Int
) {
    private var roundStatus: RoundStatus = RoundStatus.COMMAND_INPUT_STARTED


    fun endCommandInputPhase() {
        if (roundStatus != RoundStatus.COMMAND_INPUT_STARTED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.COMMAND_INPUT_STARTED}")
        }
        // do stuff here
        roundStatus = RoundStatus.COMMAND_INPUT_ENDED
    }

    fun deliverBlockingCommandsToRobot() {
        if (roundStatus != RoundStatus.COMMAND_INPUT_ENDED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.COMMAND_INPUT_ENDED}")
        }
        // do stuff here
        roundStatus = RoundStatus.BLOCKING_COMMANDS_DISPATCHED
    }

    fun deliverTradingCommandsToRobot() {
        if (roundStatus != RoundStatus.BLOCKING_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.BLOCKING_COMMANDS_DISPATCHED}")
        }
        // do stuff here
        roundStatus = RoundStatus.TRADING_COMMANDS_DISPATCHED
    }

    fun deliverMovementCommandsToRobot() {
        if (roundStatus != RoundStatus.TRADING_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.TRADING_COMMANDS_DISPATCHED}")
        }
        // do stuff here
        roundStatus = RoundStatus.MOVEMENT_COMMANDS_DISPATCHED
    }

    fun deliverBattleCommandsToRobot() {
        if (roundStatus != RoundStatus.MOVEMENT_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.MOVEMENT_COMMANDS_DISPATCHED}")
        }
        // do stuff here
        roundStatus = RoundStatus.BATTLE_COMMANDS_DISPATCHED
    }

    fun deliverMiningCommandsToRobot() {
        if (roundStatus != RoundStatus.BATTLE_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.BATTLE_COMMANDS_DISPATCHED}")
        }
        // do stuff here
        roundStatus = RoundStatus.MINING_COMMANDS_DISPATCHED
    }

    fun deliverScoutingCommandsToRobot() {
        if (roundStatus != RoundStatus.MINING_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.MINING_COMMANDS_DISPATCHED}")
        }
        // do stuff here
        roundStatus = RoundStatus.SCOUTING_COMMANDS_DISPATCHED
    }

    fun endRound() {
        roundStatus = RoundStatus.ROUND_ENDED
        // do stuff here
    }

    fun getRoundNumber(): Int = roundNumber

    fun getRoundStatus(): RoundStatus = roundStatus
}