package microservice.dungeon.game.aggregates.command.domain

import microservice.dungeon.game.aggregates.command.controller.dto.CommandRequestDto
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.round.domain.Round
import mu.KotlinLogging
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "commands",
    indexes = [
        Index(name = "command_index_roundIdAndCommandType", columnList = "round_id, command_type"),
        Index(name = "command_index_roundIdAndPlayerId", columnList = "round_id, player_id")
    ]
)
class Command constructor(
    @Id
    @Column(name = "command_id")
    @Type(type = "uuid-char")
    private val commandId: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "round_id")
    private val round: Round,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private val player: Player,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "robot_id")
    private val robot: Robot?,

    @Column(name = "command_type")
    private val commandType: CommandType,

    @Embedded
    private val commandPayload: CommandPayload,
) {
    fun getCommandId(): UUID = commandId

    fun getRound(): Round = round

    fun getPlayer(): Player = player

    fun getRobot(): Robot? = robot

    fun getCommandType(): CommandType = commandType

    fun getCommandPayload(): CommandPayload = commandPayload

    override fun toString(): String {
        return "Command(commandId=$commandId, roundId=${round.getRoundId()}, roundNumber=${round.getRoundNumber()}, player=${player.getUserName()}, robot=${robot?.getRobotId()}, commandType='$commandType', commandPayload=$commandPayload)"
    }

    companion object {
        private val logger = KotlinLogging.logger {}

        fun <A> parseCommandsToDto(commands: List<Command>, mapper: (Command) -> A): List<A> {
            val mappedCommands: MutableList<A> = mutableListOf()
            commands.forEach {
                try {
                    mappedCommands.add(mapper(it))
                } catch (ignored: Exception) { }
            }
            return mappedCommands.toList()
        }

        @Throws(CommandArgumentException::class)
        fun makeCommandFromDto(round: Round, player: Player, robot: Robot?, commandType: CommandType, dto: CommandRequestDto): Command {
            if (commandType != CommandType.BUYING && commandType != CommandType.SELLING  && robot == null) {
                logger.debug("Command-Creation failed. Robot not found but is required for this type of command. [commandType={}]", commandType)
                logger.trace{ dto.toString() }
                throw CommandArgumentException("Command-Creation failed. Robot not found but is required for this type of command. [commandType=$commandType]")
            }
            return Command(
                commandId = UUID.randomUUID(),
                round = round,
                player = player,
                robot = robot,
                commandType = commandType,
                commandPayload = CommandPayload(
                    planetId = dto.commandObject.planetId,
                    targetId = dto.commandObject.targetId,
                    itemName = dto.commandObject.itemName,
                    itemQuantity = dto.commandObject.itemQuantity
                )
            )
        }
    }
}