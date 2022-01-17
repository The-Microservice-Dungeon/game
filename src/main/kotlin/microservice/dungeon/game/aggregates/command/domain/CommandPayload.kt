package microservice.dungeon.game.aggregates.command.domain

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class CommandPayload(
    @Column(name = "PAYLOAD_PLANET_ID")
    @Type(type = "uuid-char")
    private var planetId: UUID?,

    @Column(name = "PAYLOAD_TARGET_ID")
    @Type(type = "uuid-char")
    private var targetId: UUID?,

    @Column(name = "PAYLOAD_ITEM_NAME")
    private var itemName: String?,

    @Column(name = "PAYLOAD_ITEM_QUANTITY")
    private var itemQuantity: Int?
) {
    fun getPlanetId(): UUID? = planetId

    fun getTargetId(): UUID? = targetId

    fun getItemName(): String? = itemName

    fun getItemQuantity(): Int? = itemQuantity
}