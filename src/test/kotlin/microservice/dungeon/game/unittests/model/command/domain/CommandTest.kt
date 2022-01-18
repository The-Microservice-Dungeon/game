package microservice.dungeon.game.unittests.model.command.domain

import microservice.dungeon.game.aggregates.command.controller.dto.CommandObjectRequestDto
import microservice.dungeon.game.aggregates.command.controller.dto.CommandRequestDto
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandArgumentException
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.robot.domain.RobotStatus
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.*

class CommandTest {

    private var player: Player? = null
    private var game: Game? = null
    private var round: Round? = null
    private var robot: Robot? = null

    @BeforeEach
    fun setUp() {
        player = Player("dadepu", "dadepu@smail.th-koeln.de")
        game = Game(1,5)
        game!!.startGame()
        game!!.startNewRound()
        round = game!!.getCurrentRound()!!
        robot = Robot(UUID.randomUUID(), player!!, RobotStatus.ACTIVE)
    }

    @ParameterizedTest
    @EnumSource(
        value = CommandType::class,
        names = ["SELLING", "BUYING"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun shouldThrowArgumentExceptionWhenRobotIsNullButRequired(commandType: CommandType) {
        // when then
        assertThrows(CommandArgumentException::class.java) {
            val blockingCommand = Command.makeCommandFromDto(round!!, player!!, null, commandType, CommandRequestDto(
                game!!.getGameId(),
                player!!.getPlayerToken(),
                null,
                commandType = CommandType.getStringFromType(commandType),
                CommandObjectRequestDto(
                    CommandType.getStringFromType(commandType),
                    null,
                    null,
                    null,
                    null
                )
            ))
        }
    }

    @ParameterizedTest
    @EnumSource(
        value = CommandType::class,
        names = ["SELLING", "BUYING"],
        mode = EnumSource.Mode.INCLUDE
    )
    fun shouldCreateCommandWithoutRobot(commandType: CommandType) {
        // when then
        val blockingCommand = Command.makeCommandFromDto(round!!, player!!, null, commandType, CommandRequestDto(
            game!!.getGameId(),
            player!!.getPlayerToken(),
            null,
            commandType = CommandType.getStringFromType(commandType),
            CommandObjectRequestDto(
                CommandType.getStringFromType(commandType),
                null,
                null,
                null,
                null
            )
        ))
    }
}