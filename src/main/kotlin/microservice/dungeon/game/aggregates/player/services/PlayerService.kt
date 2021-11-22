package microservice.dungeon.game.aggregates.player.services

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlayerService @Autowired constructor(
    private val playerRepository: PlayerRepository,
    private val eventPublisherService: EventPublisherService
) {
    fun createNewPlayer(userName: String, mailAddress: String): Player {
        if(!playerRepository.findPlayerByUserNameOrMailAddress(userName, mailAddress).isEmpty) {
            throw EntityAlreadyExistsException("Player already exists")
        }
        val player = Player(userName, mailAddress)
        playerRepository.save(player)
        return player
    }
}