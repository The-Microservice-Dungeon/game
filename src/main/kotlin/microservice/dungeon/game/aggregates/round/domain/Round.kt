package microservice.dungeon.game.aggregates.round.domain

import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.game.domain.Game
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ROUNDS")
class Round(
    @Id
    @Type(type="uuid-char")
    @Column(name = "ROUND_ID")
    private var roundId: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GAME_ID")
    private var game: Game,

    @Column(name = "ROUND_NUMBER")
    private val roundNumber: Int,

    @Column(name = "ROUND_STATUS")
    private var roundStatus: RoundStatus = RoundStatus.COMMAND_INPUT_STARTED,

    @Column(name = "ROUND_STARTED")
    private var roundStarted: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
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

    fun deliverSellingCommandsToRobot() {
        if (roundStatus != RoundStatus.BLOCKING_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.BLOCKING_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.SELLING_COMMANDS_DISPATCHED
    }

    fun deliverBuyingCommandsToRobot() {
        if (roundStatus != RoundStatus.SELLING_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.SELLING_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.BUYING_COMMANDS_DISPATCHED
    }

    fun deliverMovementItemUseCommandsToRobot() {
        if (roundStatus != RoundStatus.BUYING_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.BUYING_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.MOVEMENT_ITEM_USE_COMMANDS_DISPATCHED
    }

    fun deliverMovementCommandsToRobot() {
        if (roundStatus != RoundStatus.MOVEMENT_ITEM_USE_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.MOVEMENT_ITEM_USE_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.MOVEMENT_COMMANDS_DISPATCHED
    }

    fun deliverBattleItemUseCommandsToRobot() {
        if (roundStatus != RoundStatus.MOVEMENT_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.MOVEMENT_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.BATTLE_ITEM_USE_COMMANDS_DISPATCHED
    }

    fun deliverBattleCommandsToRobot() {
        if (roundStatus != RoundStatus.BATTLE_ITEM_USE_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.BATTLE_ITEM_USE_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.BATTLE_COMMANDS_DISPATCHED
    }

    fun deliverMiningCommandsToRobot() {
        if (roundStatus != RoundStatus.BATTLE_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.BATTLE_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.MINING_COMMANDS_DISPATCHED
    }

    fun deliverRepairItemUseCommandsToRobot() {
        if (roundStatus != RoundStatus.MINING_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.MINING_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.REPAIR_ITEM_USE_COMMANDS_DISPATCHED
    }

    fun deliverRegeneratingCommandsToRobot() {
        if (roundStatus != RoundStatus.REPAIR_ITEM_USE_COMMANDS_DISPATCHED) {
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.REPAIR_ITEM_USE_COMMANDS_DISPATCHED}")
        }
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

    fun getGameId(): UUID = game.getGameId()

    fun getGame(): Game = game

    fun getRoundNumber(): Int = roundNumber

    fun getRoundStatus(): RoundStatus = roundStatus

    fun getRoundStarted(): LocalDateTime = roundStarted

    override fun toString(): String {
        return "Round(roundId=$roundId, gameId=${game.getGameId()}, roundNumber=$roundNumber, roundStatus=$roundStatus)"
    }

    fun isEqualByVal(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Round

        if (roundId != other.roundId) return false
        if (game.getGameId() != other.game.getGameId()) return false
        if (roundNumber != other.roundNumber) return false
        if (roundStatus != other.roundStatus) return false

        return true
    }
}