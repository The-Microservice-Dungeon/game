package microservice.dungeon.game.aggregates.round.domain

import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import mu.KotlinLogging
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "rounds",
    uniqueConstraints = [
        UniqueConstraint(name = "round_unique_gameIdAndRoundNumber", columnNames = ["game_id", "round_number"]),
    ]
)
class Round(
    @Id
    @Type(type="uuid-char")
    @Column(name = "round_id")
    private var roundId: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private var game: Game,

    @Column(name = "round_number")
    private val roundNumber: Int,

    @Column(name = "round_status")
    private var roundStatus: RoundStatus = RoundStatus.COMMAND_INPUT_STARTED,

    @Column(name = "round_started")
    private var roundStarted: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun endCommandInputPhase() {
        if (roundStatus != RoundStatus.COMMAND_INPUT_STARTED) {
            logger.warn("Failed to set Round-Status to COMMAND_INPUT_ENDED. [roundNumber=$roundNumber, roundStatus=$roundStatus]")
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.COMMAND_INPUT_STARTED}")
        }
        roundStatus = RoundStatus.COMMAND_INPUT_ENDED
        logger.debug("Round-Status set to $roundStatus. [roundNumber=$roundNumber]")
    }

    fun deliverBlockingCommandsToRobot() {
        if (roundStatus != RoundStatus.COMMAND_INPUT_ENDED) {
            logger.warn("Failed to set Round-Status to BLOCKING_COMMANDS_DISPATCHED. [roundNumber=$roundNumber, roundStatus=$roundStatus]")
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.COMMAND_INPUT_ENDED}")
        }
        roundStatus = RoundStatus.BLOCKING_COMMANDS_DISPATCHED
        logger.debug("Round-Status set to $roundStatus. [roundNumber=$roundNumber]")
    }

    fun deliverSellingCommandsToRobot() {
        if (roundStatus != RoundStatus.BLOCKING_COMMANDS_DISPATCHED) {
            logger.warn("Failed to set Round-Status to SELLING_COMMANDS_DISPATCHED. [roundNumber=$roundNumber, roundStatus=$roundStatus]")
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.BLOCKING_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.SELLING_COMMANDS_DISPATCHED
        logger.debug("Round-Status set to $roundStatus. [roundNumber=$roundNumber]")
    }

    fun deliverBuyingCommandsToRobot() {
        if (roundStatus != RoundStatus.SELLING_COMMANDS_DISPATCHED) {
            logger.warn("Failed to set Round-Status to BUYING_COMMANDS_DISPATCHED. [roundNumber=$roundNumber, roundStatus=$roundStatus]")
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.SELLING_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.BUYING_COMMANDS_DISPATCHED
        logger.debug("Round-Status set to $roundStatus. [roundNumber=$roundNumber]")
    }

 /*   fun deliverMovementItemUseCommandsToRobot() {
        if (roundStatus != RoundStatus.BUYING_COMMANDS_DISPATCHED) {
            logger.warn("Failed to set Round-Status to MOVEMENT_ITEM_USE_COMMANDS_DISPATCHED. [roundNumber=$roundNumber, roundStatus=$roundStatus]")
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.BUYING_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.MOVEMENT_ITEM_USE_COMMANDS_DISPATCHED
        logger.debug("Round-Status set to $roundStatus. [roundNumber=$roundNumber]")
    }
*/
    fun deliverMovementCommandsToRobot() {
        if (roundStatus != RoundStatus.MOVEMENT_ITEM_USE_COMMANDS_DISPATCHED) {
            logger.warn("Failed to set Round-Status to MOVEMENT_COMMANDS_DISPATCHED. [roundNumber=$roundNumber, roundStatus=$roundStatus]")
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.MOVEMENT_ITEM_USE_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.MOVEMENT_COMMANDS_DISPATCHED
        logger.debug("Round-Status set to $roundStatus. [roundNumber=$roundNumber]")
    }
/*
    fun deliverBattleItemUseCommandsToRobot() {
        if (roundStatus != RoundStatus.MOVEMENT_COMMANDS_DISPATCHED) {
            logger.warn("Failed to set Round-Status to BATTLE_ITEM_USE_COMMANDS_DISPATCHED. [roundNumber=$roundNumber, roundStatus=$roundStatus]")
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.MOVEMENT_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.BATTLE_ITEM_USE_COMMANDS_DISPATCHED
        logger.debug("Round-Status set to $roundStatus. [roundNumber=$roundNumber]")
    }
*/
    fun deliverBattleCommandsToRobot() {
        if (roundStatus != RoundStatus.BATTLE_ITEM_USE_COMMANDS_DISPATCHED) {
            logger.warn("Failed to set Round-Status to BATTLE_COMMANDS_DISPATCHED. [roundNumber=$roundNumber, roundStatus=$roundStatus]")
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.BATTLE_ITEM_USE_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.BATTLE_COMMANDS_DISPATCHED
        logger.debug("Round-Status set to $roundStatus. [roundNumber=$roundNumber]")
    }

    fun deliverMiningCommandsToRobot() {
        if (roundStatus != RoundStatus.BATTLE_COMMANDS_DISPATCHED) {
            logger.warn("Failed to set Round-Status to MINING_COMMANDS_DISPATCHED. [roundNumber=$roundNumber, roundStatus=$roundStatus]")
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.BATTLE_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.MINING_COMMANDS_DISPATCHED
        logger.debug("Round-Status set to $roundStatus. [roundNumber=$roundNumber]")
    }
/*
    fun deliverRepairItemUseCommandsToRobot() {
        if (roundStatus != RoundStatus.MINING_COMMANDS_DISPATCHED) {
            logger.warn("Failed to set Round-Status to REPAIR_ITEM_USE_COMMANDS_DISPATCHED. [roundNumber=$roundNumber, roundStatus=$roundStatus]")
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.MINING_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.REPAIR_ITEM_USE_COMMANDS_DISPATCHED
        logger.debug("Round-Status set to $roundStatus. [roundNumber=$roundNumber]")
    }
*/
    fun deliverRegeneratingCommandsToRobot() {
        if (roundStatus != RoundStatus.REPAIR_ITEM_USE_COMMANDS_DISPATCHED) {
            logger.warn("Failed to set Round-Status to REGENERATING_COMMANDS_DISPATCHED. [roundNumber=$roundNumber, roundStatus=$roundStatus]")
            throw MethodNotAllowedForStatusException("Round Status is $roundStatus but requires ${RoundStatus.REPAIR_ITEM_USE_COMMANDS_DISPATCHED}")
        }
        roundStatus = RoundStatus.REGENERATING_COMMANDS_DISPATCHED
        logger.debug("Round-Status set to $roundStatus. [roundNumber=$roundNumber]")
    }

    fun endRound(): Boolean {
        if (roundStatus == RoundStatus.ROUND_ENDED) {
            return false
        }
        roundStatus = RoundStatus.ROUND_ENDED
        logger.debug("Round-Status set to $roundStatus. [roundNumber=$roundNumber]")
        return true
    }


    fun getRoundId(): UUID = roundId

    fun getGameId(): UUID = game.getGameId()

    fun getGame(): Game = game

    fun getRoundNumber(): Int = roundNumber

    fun getRoundStatus(): RoundStatus = roundStatus

    fun getRoundStarted(): LocalDateTime = roundStarted

    fun getTimeSinceRoundStartInSeconds(): Long {
        return ChronoUnit.SECONDS.between(roundStarted, LocalDateTime.now())
    }

    override fun toString(): String {
        return "Round(roundId=$roundId, gameId=${game.getGameId()}, roundNumber=$roundNumber, roundStatus='$roundStatus')"
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