package microservice.dungeon.game.aggregates.player.services

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.events.PlayerCreated
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PlayerService @Autowired constructor(
    private val playerRepository: PlayerRepository,
    private val eventPublisherService: EventPublisherService,
    private val eventStoreService: EventStoreService
) {
    @Transactional
    fun createNewPlayer(userName: String, mailAddress: String): Player {
        if(!playerRepository.findPlayerByUserNameOrMailAddress(userName, mailAddress).isEmpty) {
            throw EntityAlreadyExistsException("Player already exists")
        }
        val player = Player(userName, mailAddress)
        val playerCreated = PlayerCreated(player)
        playerRepository.save(player)
        eventPublisherService.publishEvents(listOf(playerCreated))
        return player
    }
}