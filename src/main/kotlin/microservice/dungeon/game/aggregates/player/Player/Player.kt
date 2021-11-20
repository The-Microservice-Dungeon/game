package microservice.dungeon.game.aggregates.player.Player

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Table(name = "players", indexes = [
    Index(name = "PlayerIndex", columnList = "playerId", unique = true)
])


@Entity
class Player {
    @Id
    @Type(type="uuid-char")
    private val playerId: UUID = UUID.randomUUID()
    @get: NotBlank
    private val playerName: String = ""
}



