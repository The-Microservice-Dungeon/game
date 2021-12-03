package microservice.dungeon.game.aggregates.command.domain

import java.util.*
import javax.persistence.Embeddable

@Embeddable
open class CommandObject(

    var commandType: String,

    var planetId: UUID?,

    var targetId: UUID?,

    var itemName: String?
)