package microservice.dungeon.game.aggregates.player.services

import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.domain.PlayerAlreadyExistsException
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlayerService @Autowired constructor(
    private val playerRepository: PlayerRepository
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @Transactional
    @Throws(PlayerAlreadyExistsException::class)
    fun createNewPlayer(userName: String, mailAddress: String): Player {
        if(!playerRepository.findByUserNameOrMailAddress(userName, mailAddress).isEmpty) {
            logger.warn("Failed to create new Player. Player already exists. [name=${userName}")
            throw PlayerAlreadyExistsException()
        }

        val player = Player(userName, mailAddress)
        playerRepository.save(player)
        logger.info("New Player created. [name=${player.getUserName()}]")

        return player
    }
}