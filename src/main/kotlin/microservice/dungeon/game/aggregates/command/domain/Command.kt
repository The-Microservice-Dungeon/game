package microservice.dungeon.game.aggregates.command.domain

import microservice.dungeon.game.aggregates.command.dtos.CommandDTO
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "commands")
class Command constructor(
    @Id @Type(type = "uuid-char")
    val transactionId: UUID = UUID.randomUUID(),

    @Type(type = "uuid-char")
    val gameId: UUID,

    @Type(type = "uuid-char")
    val playerId: UUID,

    @Type(type = "uuid-char")
    val robotId: UUID?,

    val commandType: CommandType,

    @Embedded
    val commandObject: CommandObject,

    val roundNumber: Int
) {
    fun toDto(): CommandDTO = CommandDTO(gameId, playerId, robotId, commandType, commandObject)

    override fun equals(other: Any?): Boolean =
        (other is Command)
                && transactionId == other.transactionId
                && gameId == other.gameId
                && playerId == other.playerId
                && robotId == other.robotId
                && commandType == other.commandType
                && commandObject == other.commandObject
                && roundNumber == other.roundNumber

    companion object {
        fun fromDto(dto: CommandDTO, roundNumber: Int): Command = Command(
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