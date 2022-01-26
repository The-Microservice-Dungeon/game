package microservice.dungeon.game.unittests.model.command.controller.dto

import microservice.dungeon.game.aggregates.command.controller.dto.CommandObjectRequestDto
import microservice.dungeon.game.aggregates.command.controller.dto.RoundCommandResponseDto
import microservice.dungeon.game.aggregates.command.controller.dto.RoundCommandsResponseDto
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandPayload
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.round.domain.Round
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RoundCommandsResponseDtoTest {

    private var player: Player? = null
    private var game: Game? = null
    private var round: Round? = null

    @BeforeEach
    fun setUp() {
        player = Player("dadepu", "dadepu@smail.th-koeln.de")
        game = Game(1,2)
        game!!.startGame()
        game!!.startNewRound()
        round = game!!.getCurrentRound()!!
    }

    @Test
    fun shouldInitializeCorrectly() {
        // given
        val command = Command(UUID.randomUUID(), round!!, player!!, null, CommandType.BUYING, CommandPayload(
            null, null, "ROBOT", 1
        ))

        // when
        val wrapperDto = RoundCommandsResponseDto(round!!, listOf(command))

        // then
        assertThat(wrapperDto.gameId)
            .isEqualTo(game!!.getGameId())
        assertThat(wrapperDto.roundId)
            .isEqualTo(round!!.getRoundId())
        assertThat(wrapperDto.roundNumber)
            .isEqualTo(round!!.getRoundNumber())

        // and then
        val commandDto: RoundCommandResponseDto = wrapperDto.commands[0]
        assertThat(commandDto.gameId)
            .isEqualTo(game!!.getGameId())
        assertThat(commandDto.robotId)
            .isEqualTo(command.getRobot()?.getRobotId())
        assertThat(commandDto.playerId)
            .isEqualTo(player!!.getPlayerId())
        assertThat(commandDto.commandType)
            .isEqualTo(CommandType.getStringFromType(CommandType.BUYING))

        // and then
        val commandObjectDto: CommandObjectRequestDto = commandDto.commandObject
        assertThat(commandObjectDto.commandType)
            .isEqualTo(CommandType.getStringFromType(CommandType.BUYING))
        assertThat(commandObjectDto.planetId)
            .isEqualTo(commandDto.commandObject.planetId)
        assertThat(commandObjectDto.targetId)
            .isEqualTo(commandDto.commandObject.targetId)
        assertThat(commandObjectDto.itemName)
            .isEqualTo(command.getCommandPayload()!!.getItemName())
        assertThat(commandObjectDto.itemQuantity)
            .isEqualTo(command.getCommandPayload()!!.getItemQuantity())
    }
}