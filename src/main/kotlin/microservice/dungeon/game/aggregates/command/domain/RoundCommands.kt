package microservice.dungeon.game.aggregates.command.domain

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class RoundCommands constructor(
    @OneToMany
    val list: List<Command>,

    @Id
    val roundNumber: Int
)