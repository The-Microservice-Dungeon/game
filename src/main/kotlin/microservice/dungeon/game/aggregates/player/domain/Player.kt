package microservice.dungeon.game.aggregates.player.domain

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "players",
    uniqueConstraints = [
        UniqueConstraint(name = "player_unique_playerToken", columnNames = ["player_token"]),
        UniqueConstraint(name = "player_unique_userName", columnNames = ["user_name"]),
        UniqueConstraint(name = "player_unique_mailAddress", columnNames = ["mail_address"])
])
class Player constructor(
    @Id
    @Column(name="player_id")
    @Type(type="uuid-char")
    private var playerId: UUID,

    @Column(name="player_token")
    @Type(type="uuid-char")
    private var playerToken: UUID,

    @Column(name="user_name")
    private var userName: String,

    @Column(name="mail_address")
    private var mailAddress: String,
) {

    constructor(userName: String, mailAddress: String): this(
        UUID.randomUUID(), UUID.randomUUID(), userName, mailAddress
    )

    fun getPlayerId(): UUID = playerId

    fun getPlayerToken(): UUID = playerToken

    fun getUserName(): String = userName

    fun getMailAddress(): String = mailAddress
}