package microservice.dungeon.game.aggregates.command.domain

import microservice.dungeon.game.aggregates.command.dtos.CommandDTO
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "commands")
class Command constructor(
    @Id @Type(type = "uuid-char") @GeneratedValue(strategy = GenerationType.IDENTITY)
    val transactionId: UUID = UUID.randomUUID(),

    @Type(type = "uuid-char")
    val gameId: UUID,

    @Type(type = "uuid-char")
    val playerId: UUID,

    @Type(type = "uuid-char")
    val robotId: UUID,

    @Embedded(insert = "false", update = "false") //Possible breakpoint
    val commandType: CommandType,

    @Embedded
    val commandObject: CommandObject,

    val roundNumber: Int
) {
    fun toDTO(): CommandDTO = CommandDTO(gameId, playerId, robotId, commandType, commandObject)

    companion object {
        fun fromDTO(dto: CommandDTO, roundNumber: Int): Command = Command(
            transactionId = UUID.randomUUID(),
            gameId = dto.gameId,
            playerId = dto.playerId,
            robotId = dto.robotId,
            commandType = dto.commandType,
            commandObject = dto.commandObject,
            roundNumber = roundNumber
        )
    }
}