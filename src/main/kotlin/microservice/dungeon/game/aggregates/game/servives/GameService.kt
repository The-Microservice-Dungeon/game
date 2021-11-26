package microservice.dungeon.game.aggregates.game.servives

import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.domain.GameTime
import microservice.dungeon.game.aggregates.game.events.GameEnded
import microservice.dungeon.game.aggregates.game.events.GameStarted
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.web.CommandDispatcherClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*
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
        //eventStoreService.storeEvent(playerJoined)
        //eventPublisherService.publishEvents(listOf(playerJoined))
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
        val scope = MainScope()

        val roundDuration = game.getRoundDuration()
        val commandCollectDuration = game.getCommandCollectDuration()

        val fixedRateTimer = fixedRateTimer(name = "createNewRoundTimer",
            initialDelay = 0, period = roundDuration) {


            val roundID = roundService.startNewRound(gameId, roundCounter) //start new Round

            val executionDuration = (roundDuration - commandCollectDuration)/7 // Execution time for each phase


            game.setLastRoundStartedAt(LocalTime.now())
            roundCounter += 1
            game.setCurrentRoundCount(roundCounter)

            scope.launch { // create new coroutine in common thread pool
                delay(commandCollectDuration) // non-blocking delay for 45 second
                roundService.endCommandInputs(roundID)

                roundService.deliverBlockingCommands(roundID)

                delay((commandCollectDuration + executionDuration))
                roundService.deliverTradingCommands(roundID)
                delay((commandCollectDuration + executionDuration*2))
                roundService.deliverMovementCommands(roundID)
                delay((commandCollectDuration + executionDuration*3))
                roundService.deliverBattleCommands(roundID)
                delay((commandCollectDuration + executionDuration*4))
                roundService.deliverMiningCommands(roundID)
                delay((commandCollectDuration + executionDuration*5))
                roundService.endRound(roundID)

            }

        scope.cancel()

        }
        try {
            //val game: Game = gameRepository.findByGameId(gameId).get()
            val gameLength: Long = (game.getMaxRounds()!! * 60000).toLong()
            val gameLengthScope = MainScope()
            gameLengthScope.launch {
                delay(gameLength) //ehemals Thread.sleep(gameLength)
            }
            //Testen ob das so läuft?
        } finally {
            fixedRateTimer.cancel()
            closeGame(gameId)
        }
    }



    fun getGameTime(gameId: UUID): Any {
        val game: Game = gameRepository.findByGameId(gameId).get()
        val gson = Gson()
        return gson.toJson(GameTime(
                                    ChronoUnit.MINUTES.between(game.getGameStartTime() , LocalTime.now()),
                                    ChronoUnit.SECONDS.between(game.getLastRoundStartedAt(), LocalTime.now()),
                                    game.getCurrentRoundCount()
                                    )
                            )
    }


}