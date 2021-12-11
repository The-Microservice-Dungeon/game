package microservice.dungeon.game.aggregates.command.domain

import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
open class CommandObject(
    @Column(name = "object_commandType")
    var commandType: CommandType,

    @Column(name = "object_planetId")
    var planetId: UUID?,

    @Column(name = "object_targetId")
    var targetId: UUID?,

    @Column(name = "object_itemName")
    var itemName: String?,

    @Column(name = "object_itemQuantity")
    var ItemQuantity: Int?
) {
    override fun equals(other: Any?): Boolean =
        (other is CommandObject)
                && commandType == other.commandType
                && planetId == other.planetId
                && targetId == other.targetId
                && itemName == other.itemName
                && ItemQuantity == other.ItemQuantity
}