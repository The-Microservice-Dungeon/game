package microservice.dungeon.game.aggregates.player.services

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.domain.PlayerAlreadyExistsException
import microservice.dungeon.game.aggregates.player.events.PlayerCreated
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import mu.KotlinLogging
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
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun createNewPlayer(userName: String, mailAddress: String): Player {
        if(!playerRepository.findByUserNameOrMailAddress(userName, mailAddress).isEmpty) {
            logger.warn("Failed to create a new Player. Player already exists. [name=${userName}")
            throw PlayerAlreadyExistsException()
        }

        val player = Player(userName, mailAddress)
        playerRepository.save(player)
        logger.info("New Player created. [name=${player.getUserName()}]")
        return player
    }
}