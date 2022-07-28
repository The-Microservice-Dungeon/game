package microservice.dungeon.game.aggregates.game.services

import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.*
import microservice.dungeon.game.aggregates.game.events.GameStatusEvent
import microservice.dungeon.game.aggregates.game.events.GameStatusEventBuilder
import microservice.dungeon.game.aggregates.game.events.PlayerStatusEvent
import microservice.dungeon.game.aggregates.game.events.PlayerStatusEventBuilder
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.game.web.MapGameWorldsClient
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.domain.PlayerNotFoundException
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStatusEvent
import microservice.dungeon.game.aggregates.round.events.RoundStatusEventBuilder
import microservice.dungeon.game.aggregates.round.services.RoundService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.concurrent.thread


@Service
class GameService @Autowired constructor(
    private val roundService: RoundService,
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val eventStoreService: EventStoreService,
    private val eventPublisherService: EventPublisherService,
    private val mapGameWorldsClient: MapGameWorldsClient,
    private val gameStatusEventBuilder: GameStatusEventBuilder,
    private val playerStatusEventBuilder: PlayerStatusEventBuilder,
    private val roundStatusEventBuilder: RoundStatusEventBuilder
) {
    private val logger = KotlinLogging.logger {}
    private val gameClocks = mutableMapOf<UUID, GameLoopClock>()

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Throws(GameStateException::class)
    fun createNewGame(maxPlayers: Int, maxRounds: Int): Pair<UUID, Game> {
        if (gameRepository.existsByGameStatusIn(listOf(GameStatus.CREATED, GameStatus.GAME_RUNNING))) {
            logger.warn("Failed to create a new Game. An active Game already exists.")
            throw GameStateException("A new Game could not be started, because an active Game already exists.")
        }

        val transactionId = UUID.randomUUID()
        val newGame: Game = Game(maxPlayers, maxRounds)

        // Create a GameClock, but don't start it yet.
        gameClocks[newGame.getGameId()] = GameLoopClock(TimeFrame(newGame))

        gameRepository.save(newGame)
        logger.info("New Game created. [transactionId=$transactionId]")
        logger.trace(newGame.toString())

        val gameCreatedEvent: GameStatusEvent = gameStatusEventBuilder.makeGameStatusEvent(transactionId, newGame.getGameId(), GameStatus.CREATED)
        eventStoreService.storeEvent(gameCreatedEvent)
        eventPublisherService.publishEvent(gameCreatedEvent)
        logger.debug("GameStatusEvent handed to publisher & store. [transactionId=$transactionId, gameId=${newGame.getGameId()}, gameStatus=${newGame.getGameStatus()}]")

        return Pair(transactionId, newGame)
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Throws(GameStateException::class, GameNotFoundException::class, PlayerNotFoundException::class)
    fun joinGame(playerToken: UUID, gameId: UUID): UUID {
        val transactionId = UUID.randomUUID()
        val player: Player
        val game: Game

        try {
            player = playerRepository.findByPlayerToken(playerToken).get()
        } catch (e: Exception) {
            logger.warn("Failed to join Game. Player does not exist.")
            throw PlayerNotFoundException("Failed to join game. No player found for token.")
        }

        try {
            game = gameRepository.findById(gameId).get()
        } catch (e: Exception) {
            logger.warn("Failed to join game. No game was found. [gameId=$gameId]")
            throw GameNotFoundException("Failed to join game. No game was found.")
        }

        game.joinGame(player)
        gameRepository.save(game)
        logger.info("Player has joined the game. [playerName=${player.getUserName()}, numberOfPlayers=${game.getParticipatingPlayers().size}/${game.getMaxPlayers()}]")

        val playerJoinedEvent: PlayerStatusEvent = playerStatusEventBuilder.makePlayerStatusEvent(transactionId, player.getPlayerId(), player.getUserName())
        eventStoreService.storeEvent(playerJoinedEvent)
        eventPublisherService.publishEvent(playerJoinedEvent)
        logger.debug("PlayerStatusEvent handed to publisher & store. [transactionId=$transactionId, playerId=${player.getPlayerId()}, playerUsername=${player.getUserName()}]")

        return transactionId
    }

    @Transactional
    @Throws(GameStateException::class, GameNotFoundException::class)
    fun startGame(gameId: UUID): UUID {
        val transactionId: UUID = UUID.randomUUID()
        val game: Game

        try {
            game = gameRepository.findById(gameId).get()
        } catch (e: Exception) {
            logger.warn("Failed to start the game. No game was found. [gameId=$gameId]")
            throw GameNotFoundException("Failed to start game. No game was found.")
        }

        game.startGame()
        mapGameWorldsClient.createNewGameWorld(game.getNumberJoinedPlayers())
        gameRepository.save(game)
        logger.info("Game started. [gameId=$gameId]")

        var maybeClock = gameClocks[gameId]
        if (maybeClock == null) {
            maybeClock = GameLoopClock(TimeFrame(game))
            gameClocks[gameId] = maybeClock
        }
        val clock = maybeClock

        clock.registerOnRoundEnd {
            endRound(game.getCurrentRound()!!)
        }
        clock.registerOnRoundStart {
            try {
                game.startNewRound()
            }
            // TODO: Exception is also thrown when the game ends naturally... We don't want to throw
            //  exceptions when everything works as intend. And even if, make this case catchable.
            catch (e: Exception) {
                logger.debug("Failed to start next Round: {}", e.message)
                logger.debug("Stopping clock")
                clock.stop()
                this.gameClocks.remove(game.getGameId())
            }
            gameRepository.save(game)
            startRound(game.getCurrentRound()!!)
        }
        clock.registerOnCommandInputEnded {
            val round = game.getCurrentRound()!!
            executeCommandsInOrder(round.getRoundId())
        }

        // Thread will be interrupted when clock stops.
        thread(start = true, isDaemon = false) {
            Thread.sleep(1000)
            clock.run()
        }

        val gameStartedEvent: GameStatusEvent = gameStatusEventBuilder.makeGameStatusEvent(transactionId, gameId, GameStatus.GAME_RUNNING)
        eventStoreService.storeEvent(gameStartedEvent)
        eventPublisherService.publishEvent(gameStartedEvent)
        logger.debug("GameStatusEvent handed to publisher & store. [transactionId=$transactionId, gameId=${gameId}, gameStatus=${GameStatus.GAME_RUNNING}]")

        return transactionId;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Throws(GameStateException::class, GameNotFoundException::class)
    fun endGame(gameId: UUID): UUID {
        val transactionId: UUID = UUID.randomUUID()
        val game: Game

        try {
            game = gameRepository.findById(gameId).get()
        } catch (e: Exception) {
            logger.warn("Failed to end the game. No game was found. [gameId=$gameId]")
            throw GameNotFoundException("Failed to end the game. No game was found.")
        }

        game.endGame()
        gameRepository.save(game)

        gameClocks[gameId]?.let { it.stop() }
        gameClocks.remove(gameId)

        val gameEndedEvent: GameStatusEvent = gameStatusEventBuilder.makeGameStatusEvent(transactionId, game.getGameId(), GameStatus.GAME_FINISHED)
        logger.debug("Handing GameStatusEvent off to EventPublisher & -Store. [transactionId={}, gameId={}, gameStatus={}]",
            transactionId, gameId, GameStatus.GAME_FINISHED)
        eventStoreService.storeEvent(gameEndedEvent)
        eventPublisherService.publishEvent(gameEndedEvent)

        logger.info("Game ended. Game will shutdown after round is completed. [gameId=$gameId]")
        return transactionId
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Throws(GameNotFoundException::class, GameStateException::class, IllegalArgumentException::class)
    fun changeMaximumNumberOfRounds(gameId: UUID, maxRounds: Int): UUID {
        val transactionId = UUID.randomUUID()
        val game: Game

        try {
            game = gameRepository.findById(gameId).get()
        } catch (e: Exception) {
            logger.warn("Game not found while trying to change the maximum number of rounds. [gameId=$gameId]")
            throw GameNotFoundException("Game not found. [gameId=$gameId]")
        }

        game.changeMaximumNumberOfRounds(maxRounds)
        gameRepository.save(game)
        logger.debug("Saved game with updated number of maximum rounds.")
        logger.info("Updated maximum number of rounds. [maxRounds=$maxRounds, gameId=$gameId]")
        return transactionId
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Throws(GameNotFoundException::class, GameStateException::class, IllegalArgumentException::class)
    fun changeRoundDuration(gameId: UUID, duration: Long): UUID {
        val transactional = UUID.randomUUID()
        val game = gameRepository.findById(gameId).orElseThrow { GameNotFoundException("Game not found. [gameId=$gameId]") }
        game.changeRoundDuration(duration)
        gameRepository.save(game)
        val clock = gameClocks[gameId];
        if (clock != null) {
            clock.patchTimeFrame(TimeFrame(game))
        } else {
            logger.warn { "A Game clock for $gameId cannot be found. This should not occur unless the game is not created properly or maybe the service crashed?" }
        }
        logger.info("Updated round duration. [newDurationInMillis=$duration, gameId=$gameId]")
        return transactional
    }

    private fun executeCommandsInOrder(roundId: UUID) {
        logger.info("Dispatching commands in order...")
        // TODO: This thingy here publishes the command input ended - but everything else is done here
        roundService.endCommandInputs(roundId)

      //  roundService.deliverBlockingCommands(roundId)
        roundService.deliverTradingCommands(roundId)
        roundService.deliverMovementCommands(roundId)
        roundService.deliverBattleCommands(roundId)
        roundService.deliverMiningCommands(roundId)
        roundService.deliverRegeneratingCommands(roundId)
        logger.info("Command dispatching completed.")
    }

    private fun startRound(round: Round) {
        val transactionId = UUID.randomUUID()
        logger.trace("Start-Round transactionId={}", transactionId)

        val roundEvent: RoundStatusEvent = roundStatusEventBuilder.makeRoundStatusEvent(
            transactionId, round.getGameId(), round.getRoundId(), round.getRoundNumber(), RoundStatus.COMMAND_INPUT_STARTED
        )
        logger.debug("Handing RoundStatusEvent off to EventPublisher & -Store. [transactionId={}, roundNumber={}, roundStatus={}]",
            transactionId, roundEvent.roundId, roundEvent.roundStatus)
        eventStoreService.storeEvent(roundEvent)
        eventPublisherService.publishEvent(roundEvent)

        logger.info("Round {} started.", round.getRoundNumber())
    }

    private fun endRound(round: Round) {
        this.roundService.endRound(round.getRoundId())
    }
}



//    @Transactional
//    fun createNewGame(game: Game): Game {
//        val newGame = Game()
//        newGame.setMaxRounds(game.getMaxRounds())
//        newGame.setMaxPlayers(game.getMaxPlayers())
//        newGame.setRoundDuration(game.getRoundDuration())
//        var commandCollectionTime = game.getRoundDuration().toDouble()
//        commandCollectionTime *= 0.75
//        newGame.setCommandCollectDuration(commandCollectionTime)
//        gameRepository.save(newGame)
//        val gameCreated = GameCreated(newGame)
//        eventStoreService.storeEvent(gameCreated)
//        eventPublisherService.publishEvents(listOf(gameCreated))
//        return newGame
//    }
//
//    @Transactional
//    fun closeGame(gameId: UUID) {
//        val game: Game = gameRepository.findByGameId(gameId).get()
//        game.endGame()
//        gameRepository.save(game)
//        val gameEnded = GameEnded(game)
//        eventStoreService.storeEvent(gameEnded)
//        eventPublisherService.publishEvents(listOf(gameEnded))
//    }
//
//
//    fun getAllGames(): MutableIterable<Game> = gameRepository.findAll()
//
//
//    @Transactional
//    fun insertPlayer(gameId: UUID, token: UUID): ResponseEntity<PlayerJoinGameDto> {
//        val transactionId = UUID.randomUUID()
//
//        val player: Player = playerRepository.findByPlayerToken(token).get() // is empty?
//            ?: throw EntityNotFoundException("Player does not exist")
//
//
//
//        val game: Game = gameRepository.findByGameId(gameId).get()
//
//        val playersInGame = PlayersInGame(
//            player.getPlayerId(),
//            gameId = game.getGameId()
//        )
//
//        val playerAlreadyInGame: PlayersInGame? = game.playerList.find { it == playersInGame }
//
//        if (game.getGameStatus() != GameStatus.CREATED) {
//            throw MethodNotAllowedForStatusException("For Player to join requires game status ${GameStatus.CREATED}, but game status is ${game.getGameStatus()}")
//
//        } else if (playerAlreadyInGame != null) {
//
//            throw EntityAlreadyExistsException("Player is already in game")
//
//        } else if (game.playerList.size < game.getMaxPlayers()) {
//            //           game.addPlayersToGame(PlayersInGame(playerId = player.getPlayerId(), gameId = game.getGameId()))
//            gameRepository.save(PlayersInGame(playerId = player.getPlayerId(), gameId = game.getGameId()))
//            game.playerList.add(PlayersInGame(playerId = player.getPlayerId(), gameId = game.getGameId()))
//            gameRepository.save(game)
//
//            val playerJoined = PlayerJoined(game,player, transactionId)
//            eventStoreService.storeEvent(playerJoined)
//            eventPublisherService.publishEvents(listOf(playerJoined))
//
//            val responseDto = PlayerJoinGameDto(transactionId)
//            return ResponseEntity(responseDto, HttpStatus.OK)
//
//        } else {
//            throw GameAlreadyFullException("Game is full")
//        }
//
//    }
//
//    @Transactional
//    fun runGame(gameId: UUID) {
//        val game: Game = gameRepository.findByGameId(gameId).get()
//
//        game.startGame()
//
//        val gameStarted = GameStarted(game)
//        eventStoreService.storeEvent(gameStarted)
//        eventPublisherService.publishEvents(listOf(gameStarted))
//
//        mapGameWorldsClient.createNewGameWorld(game.getPlayersInGame().size)
//
//        var roundCounter = 1
//        val scope = CoroutineScope(Dispatchers.Unconfined)
//        val roundScope = CoroutineScope(Dispatchers.Default)
//
//        val maxRounds = game.getMaxRounds()
//
//        val roundDuration = game.getRoundDuration()
//        val commandCollectDuration = game.getCommandCollectDuration()
//
//
//        scope.launch {
//            while (roundCounter != (maxRounds + 1) && (game.getGameStatus() != GameStatus.GAME_FINISHED)) {
//
//                val roundId = roundService.startNewRound(gameId, roundCounter)
//
//                if (roundCounter == 1){
//                    eventPublisherService.publishEvents(eventStoreService.fetchUnpublishedEvents())
//                }
//
//                val executionDuration = (roundDuration - commandCollectDuration) / 7
//
//                //END COLLECTIONPHASE START BLOCKING
//
//                delay(commandCollectDuration.toLong())
//                roundService.run {
//                    endCommandInputs(roundId)
//                    deliverBlockingCommands(roundId)
//                }
//
//
//                //START TRADING
//
//                delay(executionDuration.toLong())
//                roundService.run {
//                    deliverTradingCommands(roundId)
//                }
//
//
//                //START Moving
//
//                delay(executionDuration.toLong())
//                roundService.run {
//                    deliverMovementCommands(roundId)
//                }
//
//
//                //START Battle
//
//                delay(executionDuration.toLong())
//                roundService.run {
//                    deliverBattleCommands(roundId)
//                }
//
//
//                //START Mining
//
//                delay(executionDuration.toLong())
//                roundService.run {
//                    deliverMiningCommands(roundId)
//                }
//
//
//                //START Regenerating
//
//                delay(executionDuration.toLong())
//                roundService.run {
//                    deliverRegeneratingCommands(roundId)
//                }
//
//
//                //END Round
//
//                delay(executionDuration.toLong())
//                roundService.run {
//                    endRound(roundId)
//                }
//
//
//
//                roundCounter += 1
//
//                if (roundCounter == (maxRounds + 1)) {
//                    closeGame(gameId)
//                    scope.cancel()
//                }
//            }
//        }
//    }
//
//
//
//
//
//
//    fun getGameTime(gameId: UUID): GameTimeDto {
//        val game: Game = gameRepository.findByGameId(gameId).get()
//        return GameTimeDto(
//            ChronoUnit.MINUTES.between(game.getGameStartTime(), LocalTime.now()),
//            ChronoUnit.SECONDS.between(game.getLastRoundStartedAt(), LocalTime.now()),
//            game.getCurrentRoundCount()
//        )
//    }
//
//    fun patchMaxRound(id: UUID, maxRounds: Int) {
//
//        val game: Game = gameRepository.findByGameId(id).get()
//        game.setMaxRounds(maxRounds)
//
//        gameRepository.save(game)
//
//
//
//    }
//
//    fun patchRoundDuration(id: UUID, newDuration: Long){
//        val game: Game = gameRepository.findByGameId(id).get()
//        game.setRoundDuration(newDuration)
//
//        var commandCollectionTime = game.getRoundDuration().toDouble()
//        commandCollectionTime *= 0.75
//        game.setCommandCollectDuration(commandCollectionTime)
//
//        gameRepository.save(game)
//
//
//    }
//
//
//}

