package microservice.dungeon.game.aggregates.game.servives

import kotlinx.coroutines.*
import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.core.EntityNotFoundException
import microservice.dungeon.game.aggregates.core.GameAlreadyFullException
import microservice.dungeon.game.aggregates.core.MethodNotAllowedForStatusException
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameStatus
import microservice.dungeon.game.aggregates.game.domain.PlayersInGame
import microservice.dungeon.game.aggregates.game.dtos.GameResponseDto
import microservice.dungeon.game.aggregates.game.dtos.GameTimeDto
import microservice.dungeon.game.aggregates.game.events.GameEnded
import microservice.dungeon.game.aggregates.game.events.GameStarted
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.dtos.PlayerResponseDto
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.thread


@Service
class GameService @Autowired constructor(
    private val roundService: RoundService,
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val eventStoreService: EventStoreService,
    private val eventPublisherService: EventPublisherService
) {
    @Transactional
    fun createNewGame(game: Game): Game {
        val newGame = Game()
        newGame.setMaxRounds(game.getMaxRounds())
        newGame.setMaxPlayers(game.getMaxPlayers())
        newGame.setRoundDuration(game.getRoundDuration())
        var commandCollectionTime = game.getRoundDuration().toDouble()
        commandCollectionTime *= 0.75
        newGame.setCommandCollectDuration(commandCollectionTime)
        gameRepository.save(newGame)
        val gameStarted = GameStarted(game)
//        eventStoreService.storeEvent(gameStarted)
        //       eventPublisherService.publishEvents(listOf(gameStarted))
        return newGame
    }

//    @Transactional
    //   fun addPlayerToGame(game: Game, token: UUID) {
    //       val player = PlayersInGame(token, game)
//        gameRepository.save(player)
//    }

    @Transactional
    fun closeGame(gameId: UUID) {
        val game: Game = gameRepository.findByGameId(gameId).get()
        game.endGame()
        gameRepository.save(game)
        val gameEnded = GameEnded(game)
        //       eventStoreService.storeEvent(gameEnded)
        //       eventPublisherService.publishEvents(listOf(gameEnded))
    }


    fun getAllGames(): MutableIterable<Game> = gameRepository.findAll()


    @Transactional
    fun insertPlayer(gameId: UUID, token: UUID): ResponseEntity<PlayerResponseDto> {

        val player: Player = playerRepository.findByPlayerToken(token).get() // is empty?
            ?: throw EntityNotFoundException("Player does not exist")


        val responsePlayer = PlayerResponseDto(//nochmal anascheun warum es hier ist
            player.getPlayerToken(),
            player.getUserName(),
            player.getMailAddress()
        )

        val game: Game = gameRepository.findByGameId(gameId).get()

        val playersInGame = PlayersInGame(
            player.getPlayerId(),
            gameId = game.getGameId()
        )

        val playerAlreadyInGame: PlayersInGame? = game.playerList.find { it == playersInGame }

        if (game.getGameStatus() != GameStatus.CREATED) {
            throw MethodNotAllowedForStatusException("For Player to join requires game status ${GameStatus.CREATED}, but game status is ${game.getGameStatus()}")

        } else if (playerAlreadyInGame != null) {

            throw EntityAlreadyExistsException("Player is already in game")

        } else if (game.playerList.size < game.getMaxPlayers()) {
            //           game.addPlayersToGame(PlayersInGame(playerId = player.getPlayerId(), gameId = game.getGameId()))
            gameRepository.save(PlayersInGame(playerId = player.getPlayerId(), gameId = game.getGameId()))
            game.playerList.add(PlayersInGame(playerId = player.getPlayerId(), gameId = game.getGameId()))
            gameRepository.save(game)
            return ResponseEntity(responsePlayer, HttpStatus.CREATED)
            //eventStoreService.storeEvent(playerJoined)
            //eventPublisherService.publishEvents(listOf(playerJoined))
        } else {
            throw GameAlreadyFullException("Game is full")
        }

    }

    @Transactional
    fun runGame(gameId: UUID) {
        val game: Game = gameRepository.findByGameId(gameId).get()
        game.startGame()
        var roundCounter = 1
        val scope = CoroutineScope(Dispatchers.Unconfined)
        val roundScope = CoroutineScope(Dispatchers.Default)

        val maxRounds = game.getMaxRounds()

        val roundDuration = game.getRoundDuration()
        val commandCollectDuration = game.getCommandCollectDuration()


        scope.launch {
            while (roundCounter != (maxRounds + 1)) {

                //NOT UPDATING AFTER FIRST ROUND put into Round!
                game.setLastRoundStartedAt(LocalTime.now())
                game.setCurrentRoundCount(roundCounter)


                val roundID = roundService.startNewRound(gameId, roundCounter)
                val executionDuration = (roundDuration - commandCollectDuration) / 7

                //END COLLECTIONPHASE START BLOCKING
                roundScope.launch {
                    delay(commandCollectDuration.toLong())
                    roundService.run {
                        endCommandInputs(roundID)
                        deliverBlockingCommands(roundID)
                    }
                }

                //END BLOCKING START TRADING
                roundScope.launch {
                    delay(commandCollectDuration.toLong())
                    roundService.run {
                        endCommandInputs(roundID)
                        deliverBlockingCommands(roundID)
                    }
                }

                roundScope.launch {
                    delay(commandCollectDuration.toLong())
                    roundService.run {
                        endCommandInputs(roundID)
                        deliverBlockingCommands(roundID)
                    }
                }


                roundCounter += 1

                if (roundCounter == (maxRounds + 1)) {
                    closeGame(gameId)
                    scope.cancel()
                }
            }
        }
    }






    fun getGameTime(gameId: UUID): GameTimeDto {
        val game: Game = gameRepository.findByGameId(gameId).get()
        return GameTimeDto(
            ChronoUnit.MINUTES.between(game.getGameStartTime(), LocalTime.now()),
            ChronoUnit.SECONDS.between(game.getLastRoundStartedAt(), LocalTime.now()),
            game.getCurrentRoundCount()
        )
    }

    fun patchMaxRound(id: UUID, maxRounds: Int): ResponseEntity<GameResponseDto?>? {

        val game: Game = gameRepository.findByGameId(id).get()
        game.setMaxRounds(maxRounds)

        gameRepository.save(game)

        val responseGame = GameResponseDto(
            game.getGameId(),
            game.getGameStatus(),
            game.getMaxPlayers(),
            game.getMaxRounds(),
            game.getRoundDuration(),
            game.getCommandCollectDuration(),
            game.getCreatedGameDateTime(),
        )

        return ResponseEntity<GameResponseDto?>(responseGame, HttpStatus.OK)

    }

    fun patchRoundDuration(id: UUID, newDuration: Long): ResponseEntity<GameResponseDto?>? {
        val game: Game = gameRepository.findByGameId(id).get()
        game.setRoundDuration(newDuration)

        var commandCollectionTime = game.getRoundDuration().toDouble()
        commandCollectionTime *= 0.75
        game.setCommandCollectDuration(commandCollectionTime)

        gameRepository.save(game)

        val responseGame = GameResponseDto(
            game.getGameId(),
            game.getGameStatus(),
            game.getMaxPlayers(),
            game.getMaxRounds(),
            game.getRoundDuration(),
            game.getCommandCollectDuration(),
            game.getCreatedGameDateTime(),
        )

        return ResponseEntity<GameResponseDto?>(responseGame, HttpStatus.OK)
    }


}

