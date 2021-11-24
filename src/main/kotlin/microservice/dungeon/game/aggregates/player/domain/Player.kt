package microservice.dungeon.game.aggregates.player.domain

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(name = "players", indexes = [
    Index(name = "playerUniqueIndexFromPlayerToken", columnList = "playerToken", unique = true),
    Index(name = "playerUniqueIndexFromUsername", columnList = "userName", unique = true),
    Index(name = "playerUniqueIndexFromMailAddress", columnList = "mailAddress", unique = true)
])
class Player constructor(
    private var userName: String,
    private var mailAddress: String,
    @Id
    @Type(type="uuid-char")
    var playerId: UUID = UUID.randomUUID(),
    @Type(type="uuid-char")
    private var playerToken: UUID = UUID.randomUUID()
) {
    @JvmName("getPlayerId1")
    fun getPlayerId(): UUID = playerId

    fun getPlayerToken(): UUID = playerToken

    fun getUserName(): String = userName

    fun getMailAddress(): String = mailAddress
}