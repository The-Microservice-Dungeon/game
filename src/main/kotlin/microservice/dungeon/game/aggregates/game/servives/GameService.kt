package microservice.dungeon.game.aggregates.game.servives

import com.google.gson.Gson
import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.domain.GameTime
import microservice.dungeon.game.aggregates.game.events.GameEnded
import microservice.dungeon.game.aggregates.game.events.GameStarted
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.player.Player.Player
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.web.CommandDispatcherClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer


@Service
class GameService @Autowired constructor (
    private val roundService: RoundService,
    private val gameRepository: GameRepository,
    private val roundRepository: RoundRepository,
    private val eventStoreService: EventStoreService,
    private val eventPublisherService: EventPublisherService,
    private val commandDispatcherClient: CommandDispatcherClient
) {
    @Transactional
    fun createNewGame(): Game {
        val game = Game()
        gameRepository.save(game)
        val gameStarted = GameStarted(game)
        eventStoreService.storeEvent(gameStarted)
        eventPublisherService.publishEvents(listOf(gameStarted))
        return game
    }

    @Transactional
    fun closeGame(gameId: UUID) {
        val game: Game = gameRepository.findByGameId(gameId).get()
        game.endGame()
        gameRepository.save(game)
        val gameEnded = GameEnded(game)
        eventStoreService.storeEvent(gameEnded)
        eventPublisherService.publishEvents(listOf(gameEnded))
    }


    fun getAllGames(): MutableIterable<Game> = gameRepository.findAll()


    @Transactional
    fun insertPlayer(gameId : UUID, player: Player){
        val game: Game = gameRepository.findByGameId(gameId).get()
        if (game.getGameStatus() != GameStatus.CREATED) {
            throw MethodNotAllowedForStatusException("For Player to join requires game status ${GameStatus.CREATED}, but game status is ${game.getGameStatus()}")
        }

        else if (game.playerList.size < game.getMaxPlayers()!!) {
        game.playerList.add(player)
        eventStoreService.storeEvent(playerJoined)
        eventPublisherService.publishEvents(listOf(playerJoined))
        }

        else {
                throw MethodNotAllowedForStatusException("Player cannot join")
        }

    }
    @Transactional
    fun runGame(gameId: UUID){
        val game: Game = gameRepository.findByGameId(gameId).get()
        game.startGame()
        var roundCounter = 1
        val fixedRateTimer = fixedRateTimer(name = "createNewRoundTimer",
            initialDelay = 0, period = 60000) {

            val roundID = roundService.startNewRound(gameId, roundCounter)
            game.setLastRoundStrartedAt(LocalTime.now())
            roundCounter += 1
            game.setCurrentRoundCount(roundCounter)

            Thread.sleep(45000)

            roundService.endCommandInputs(roundID)

            roundService.deliverBlockingCommands(roundID)
            Thread.sleep(2000)
            roundService.deliverTradingCommands(roundID)
            Thread.sleep(2000)
            roundService.deliverMovementCommands(roundID)
            Thread.sleep(2000)
            roundService.deliverBattleCommands(roundID)
            Thread.sleep(2000)
            roundService.deliverMiningCommands(roundID)
            Thread.sleep(2000)
            roundService.endRound(roundID)

        }
        try {
            val game: Game = gameRepository.findByGameId(gameId).get()
            val gameLength: Long = (game.getMaxRounds() * 60000).toLong()
            Thread.sleep(gameLength)
        } finally {
            fixedRateTimer.cancel()
            closeGame(gameId)
        }
    }


    fun getGameTime(gameId: UUID): Any {
        val game: Game = gameRepository.findByGameId(gameId).get()
        val round: Round = roundRepository.findByGameIdAndRoundNumber(gameId, game.getCurrentRoundCount()).get()
        val roundTime = ChronoUnit.SECONDS.between(game.getLastRoundStrartedAt(), LocalTime.now())
        val gameTime =  ChronoUnit.MINUTES.between(game.getLastRoundStrartedAt(), LocalTime.now())
        val gson = Gson()
        return gson.toJson(GameTime(gameTime, roundTime, game.getCurrentRoundCount()))
    }


}