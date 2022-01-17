package microservice.dungeon.game.unittests.model.round.domain

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.LocalDateTime
import java.util.*

class RoundTest {
    private val game = Game(10, 100)
    private val someGameId = game.getGameId()
    private val someRoundId = UUID.randomUUID()
    private val someRoundNumber = 3


    @Test
    fun shouldConstructValidStateFromDefaults() {
        val round = Round(game = game, roundNumber = someRoundNumber)

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.COMMAND_INPUT_STARTED)
        assertThat(round.getRoundStarted())
            .isBeforeOrEqualTo(LocalDateTime.now())
    }


    @Test
    fun endCommandInputPhaseShouldSetStatusToCommandInputEnded() {
        val expectedStatus = RoundStatus.COMMAND_INPUT_STARTED
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = expectedStatus)
        round.endCommandInputPhase()

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.COMMAND_INPUT_ENDED)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["COMMAND_INPUT_STARTED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun endCommandInputPhaseShouldThrowWhenStatusIsOtherThanExpected(invalidStatus: RoundStatus) {
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = invalidStatus)

        assertThatThrownBy {
            round.endCommandInputPhase()
        }
    }

    @Test
    fun deliverBlockingCommandsToRobotShouldSetStatusToBlockingCommandsDispatched() {
        val expectedStatus = RoundStatus.COMMAND_INPUT_ENDED
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = expectedStatus)
        round.deliverBlockingCommandsToRobot()

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.BLOCKING_COMMANDS_DISPATCHED)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["COMMAND_INPUT_ENDED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun deliverBlockingCommandsToRobotShouldThrowWhenStatusIsOtherThanExpected(invalidStatus: RoundStatus) {
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = invalidStatus)

        assertThatThrownBy {
            round.deliverBlockingCommandsToRobot()
        }
    }

    @Test
    fun shouldAllowToDeliverSellingCommands() {
        val expectedStatus = RoundStatus.BLOCKING_COMMANDS_DISPATCHED
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = expectedStatus)
        round.deliverSellingCommandsToRobot()

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.SELLING_COMMANDS_DISPATCHED)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["BLOCKING_COMMANDS_DISPATCHED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun shouldNotAllowToDeliverSellingCommandsWhenStatusIsOtherThenExpected(invalidStatus: RoundStatus) {
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = invalidStatus)

        assertThatThrownBy {
            round.deliverSellingCommandsToRobot()
        }
    }

    @Test
    fun shouldAllowToDeliverBuyingCommands() {
        val expectedStatus = RoundStatus.SELLING_COMMANDS_DISPATCHED
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = expectedStatus)
        round.deliverBuyingCommandsToRobot()

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.BUYING_COMMANDS_DISPATCHED)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["SELLING_COMMANDS_DISPATCHED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun shouldNotAllowToDeliverBuyingCommandsWhenStatusIsOtherThenExpected(invalidStatus: RoundStatus) {
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = invalidStatus)

        assertThatThrownBy {
            round.deliverBuyingCommandsToRobot()
        }
    }

    @Test
    fun shouldAllowToDeliverMovementItemUseCommandsToRobot() {
        val expectedStatus = RoundStatus.BUYING_COMMANDS_DISPATCHED
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = expectedStatus)
        round.deliverMovementItemUseCommandsToRobot()

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.MOVEMENT_ITEM_USE_COMMANDS_DISPATCHED)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["BUYING_COMMANDS_DISPATCHED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun shouldNotAllowMovementItemUseCommandsDeliveryWhenStatusIsOtherThenExpected(invalidStatus: RoundStatus) {
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = invalidStatus)

        assertThatThrownBy {
            round.deliverMovementItemUseCommandsToRobot()
        }
    }

    @Test
    fun deliverMovementCommandsToRobotShouldSetStatusToMovementCommandsDispatched() {
        val expectedStatus = RoundStatus.MOVEMENT_ITEM_USE_COMMANDS_DISPATCHED
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = expectedStatus)
        round.deliverMovementCommandsToRobot()

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.MOVEMENT_COMMANDS_DISPATCHED)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["MOVEMENT_ITEM_USE_COMMANDS_DISPATCHED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun deliverMovementCommandsToRobotShouldThrowWhenStatusIsOtherThenExpected(invalidStatus: RoundStatus) {
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = invalidStatus)

        assertThatThrownBy {
            round.deliverMovementCommandsToRobot()
        }
    }

    @Test
    fun shouldAllowToDeliverBattleItemUseCommandsToRobot() {
        val expectedStatus = RoundStatus.MOVEMENT_COMMANDS_DISPATCHED
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = expectedStatus)
        round.deliverBattleItemUseCommandsToRobot()

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.BATTLE_ITEM_USE_COMMANDS_DISPATCHED)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["MOVEMENT_COMMANDS_DISPATCHED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun shouldNotAllowBattleItemUseCommandsDeliveryWhenStatusIsOtherThenExpected(invalidStatus: RoundStatus) {
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = invalidStatus)

        assertThatThrownBy {
            round.deliverBattleItemUseCommandsToRobot()
        }
    }

    @Test
    fun deliverBattleCommandsToRobotShouldSetStatusToBattleCommandsDispatched() {
        val expectedStatus = RoundStatus.BATTLE_ITEM_USE_COMMANDS_DISPATCHED
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = expectedStatus)
        round.deliverBattleCommandsToRobot()

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.BATTLE_COMMANDS_DISPATCHED)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["BATTLE_ITEM_USE_COMMANDS_DISPATCHED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun deliverBattleCommandsToRobotShouldThrowWhenStatusIsOtherThenExpected(invalidStatus: RoundStatus) {
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = invalidStatus)

        assertThatThrownBy {
            round.deliverBattleCommandsToRobot()
        }
    }

    @Test
    fun deliverMiningCommandsToRobotShouldSetStatusToMiningCommandsDispatched() {
        val expectedStatus = RoundStatus.BATTLE_COMMANDS_DISPATCHED
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = expectedStatus)
        round.deliverMiningCommandsToRobot()

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.MINING_COMMANDS_DISPATCHED)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["BATTLE_COMMANDS_DISPATCHED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun deliverMiningCommandsToRobotShouldThrowWhenStatusIsOtherThenExpected(invalidStatus: RoundStatus) {
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = invalidStatus)

        assertThatThrownBy {
            round.deliverMiningCommandsToRobot()
        }
    }

    @Test
    fun shouldAllowToDeliverRepairItemUseCommands() {
        val expectedStatus = RoundStatus.MINING_COMMANDS_DISPATCHED
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = expectedStatus)
        round.deliverRepairItemUseCommandsToRobot()

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.REPAIR_ITEM_USE_COMMANDS_DISPATCHED)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["MINING_COMMANDS_DISPATCHED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun shouldNotAllowRepairItemUseCommandsDeliveryWhenStatusIsOtherThenExpected(invalidStatus: RoundStatus) {
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = invalidStatus)

        assertThatThrownBy {
            round.deliverRepairItemUseCommandsToRobot()
        }
    }

    @Test
    fun deliverRegeneratingCommandsToRobotShouldSetStatusToRegeneratingCommandsDispatched() {
        val expectedStatus = RoundStatus.REPAIR_ITEM_USE_COMMANDS_DISPATCHED
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = expectedStatus)
        round.deliverRegeneratingCommandsToRobot()

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.REGENERATING_COMMANDS_DISPATCHED)
    }

    @ParameterizedTest
    @EnumSource(
        value = RoundStatus::class,
        names = ["REPAIR_ITEM_USE_COMMANDS_DISPATCHED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun deliverRegeneratingCommandsToRobotShouldThrowWhenStatusIsOtherThenExpected(invalidStatus: RoundStatus) {
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = invalidStatus)

        assertThatThrownBy {
            round.deliverRegeneratingCommandsToRobot()
        }
    }

    @ParameterizedTest
    @EnumSource(RoundStatus::class)
    fun endRoundShouldSetStatusToRoundEnded(status: RoundStatus) {
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = status)
        round.endRound()

        assertThat(round.getRoundStatus())
            .isEqualTo(RoundStatus.ROUND_ENDED)
    }

    @ParameterizedTest
    @EnumSource(
        RoundStatus::class,
        names = ["ROUND_ENDED"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun endRoundShouldShouldBeTrueWhenStatusChangeOccurred(status: RoundStatus) {
        // given
        val round = Round(game = game, roundNumber = someRoundNumber, roundStatus = status)

        // when
        val response = round.endRound()

        // then
        assertThat(response).isTrue

        // when
        val alreadyEndedResponse = round.endRound()

        // then
        assertThat(alreadyEndedResponse).isFalse
    }
}