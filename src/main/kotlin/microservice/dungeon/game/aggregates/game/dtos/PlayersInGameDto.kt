package microservice.dungeon.game.aggregates.game.dtos

import microservice.dungeon.game.aggregates.game.domain.Game
import java.util.*

class PlayersInGameDto (
    val playerInGameId: UUID,
    val game: Game
){}



