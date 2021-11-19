package microservice.dungeon.game.aggregates.game.servives

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.web.CommandDispatcherClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
class GameService @Autowired constructor (
    private val gameRepository: GameRepository,
    private val eventStoreService: EventStoreService,
    private val eventPublisherService: EventPublisherService,
    private val commandDispatcherClient: CommandDispatcherClient
) {
    @Transactional
    fun startNewGame(gameId: UUID): UUID {
        if (!gameRepository.findByGameId(gameId).isEmpty) {
            throw EntityAlreadyExistsException("A game $gameId already exists.")
        }
        val game = Game(gameId)
        gameRepository.save(game)
        val gameStarted = GameStarted(game)
        eventStoreService.storeEvent(gameStarted)
        eventPublisherService.publishEvents(listOf(gameStarted))
        return game.getGameId()
    }


    @Transactional
    fun endGame(roundId: UUID) {
        val game: Game = GameRepository.findByGameId(gameId).get()
        game.endGame()
        gameRepository.save(game)
        val gameEnded = GameEnded(game)
        eventStoreService.storeEvent(gameEnded)
        eventPublisherService.publishEvents(listOf(gameEnded))
    }
}