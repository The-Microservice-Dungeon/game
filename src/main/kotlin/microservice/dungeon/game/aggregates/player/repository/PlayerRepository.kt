package microservice.dungeon.game.aggregates.player.repository

import microservice.dungeon.game.aggregates.player.domain.Player
import org.springframework.data.repository.CrudRepository
import java.util.*

interface PlayerRepository: CrudRepository<Player, UUID> {

    fun findByPlayerToken(playerToken: UUID): Optional<Player>

    fun findByUserNameOrMailAddress(userName: String, mailAddress: String): Optional<Player>

    fun findByUserNameAndMailAddress(userName: String, mailAddress: String): Optional<Player>
}