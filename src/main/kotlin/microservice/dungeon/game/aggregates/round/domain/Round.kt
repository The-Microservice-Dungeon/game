package microservice.dungeon.game.aggregates.round.domain

import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(name = "rounds", indexes = [
    Index(name = "roundIndexWithGameIdAndRoundNumber", columnList = "gameId, roundNumber", unique = true)
])
class Round(
    @Type(type="uuid-char")
    private val gameId: UUID,
    private val roundNumber: Int,
    @Id
    @Type(type="uuid-char")
    private val roundId: UUID = UUID.randomUUID(),
    private var roundStatus: RoundStatus = RoundStatus.COMMAND_INPUT_STARTED
) {

    fun endCommandInputPhase() {
        if (roundStatus != RoundStatus.COMMAND_INPUT_STARTED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.COMMAND_INPUT_STARTED}")
        }
        roundStatus = RoundStatus.COMMAND_INPUT_ENDED
    }

    fun deliverBlockingCommandsToRobot() {
        if (roundStatus != RoundStatus.COMMAND_INPUT_ENDED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.COMMAND_INPUT_ENDED}")
        }
        roundStatus = RoundStatus.BLOCKING_COMMANDS_DISPATCHED
    }

    fun deliverTradingCommandsToRobot() {
        if (roundStatus != RoundStatus.BLOCKING_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.BLOCKING_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.TRADING_COMMANDS_DISPATCHED
    }

    fun deliverMovementCommandsToRobot() {
        if (roundStatus != RoundStatus.TRADING_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.TRADING_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.MOVEMENT_COMMANDS_DISPATCHED
    }

    fun deliverBattleCommandsToRobot() {
        if (roundStatus != RoundStatus.MOVEMENT_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.MOVEMENT_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.BATTLE_COMMANDS_DISPATCHED
    }

    fun deliverMiningCommandsToRobot() {
        if (roundStatus != RoundStatus.BATTLE_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.BATTLE_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.MINING_COMMANDS_DISPATCHED
    }

    fun deliverRegeneratingCommandsToRobot() {
        roundStatus = RoundStatus.REGENERATING_COMMANDS_DISPATCHED
    }

    fun endRound(): Boolean {
        if (roundStatus ==  RoundStatus.ROUND_ENDED) {
            return false
        }
        roundStatus = RoundStatus.ROUND_ENDED
        return true
    }


    fun getRoundId(): UUID = roundId

    fun getGameId(): UUID = gameId

    fun getRoundNumber(): Int = roundNumber

    fun getRoundStatus(): RoundStatus = roundStatus
}