package microservice.dungeon.game.aggregates.player.domain

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "PLAYERS", indexes = [
    Index(name = "playerUniqueIndexFromPlayerToken", columnList = "PLAYER_TOKEN", unique = true),
    Index(name = "playerUniqueIndexFromUsername", columnList = "USER_NAME", unique = true),
    Index(name = "playerUniqueIndexFromMailAddress", columnList = "MAIL_ADDRESS", unique = true)
])
class Player constructor(
    @Id
    @Column(name="PLAYER_ID")
    @Type(type="uuid-char")
    private var playerId: UUID,

    @Column(name="PLAYER_TOKEN")
    @Type(type="uuid-char")
    private var playerToken: UUID,

    @Column(name="USER_NAME")
    private var userName: String,

    @Column(name="MAIL_ADDRESS")
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