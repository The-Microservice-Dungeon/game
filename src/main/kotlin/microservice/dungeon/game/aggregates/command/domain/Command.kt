package microservice.dungeon.game.aggregates.command.domain

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.robot.domain.Robot
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.events.dto.RoundStatusEventDto
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "COMMANDS")
class Command constructor(
    @Id
    @Column(name = "COMMAND_ID")
    @Type(type = "uuid-char")
    private val commandId: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROUND_ID")
    private val round: Round,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PLAYER_ID")
    private val player: Player,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROBOT_ID")
    private val robot: Robot?,

    @Column(name = "COMMAND_TYPE")
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

    companion object {
        fun <A> parseCommandsToDto(commands: List<Command>, mapper: (Command) -> A): List<A> {
            val mappedCommands: MutableList<A> = mutableListOf()
            commands.forEach {
                try {
                    mappedCommands.add(mapper(it))
                } catch (ignored: Exception) {}
            }
            return mappedCommands.toList()
        }
    }
}