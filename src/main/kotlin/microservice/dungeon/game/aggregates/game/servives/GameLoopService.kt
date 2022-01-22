package microservice.dungeon.game.aggregates.game.servives

import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.game.domain.*
import microservice.dungeon.game.aggregates.game.events.GameStatusEvent
import microservice.dungeon.game.aggregates.game.events.GameStatusEventBuilder
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStatusEvent
import microservice.dungeon.game.aggregates.round.events.RoundStatusEventBuilder
import microservice.dungeon.game.aggregates.round.services.RoundService
import mu.KotlinLogging
import java.util.*


class GameLoopService(
    private val gameRepository: GameRepository,
    private val roundService: RoundService,
    private val gameStatusEventBuilder: GameStatusEventBuilder,
    private val roundStatusEventBuilder: RoundStatusEventBuilder,
    private val eventStoreService: EventStoreService,
    private val eventPublisherService: EventPublisherService
) {
    private val logger = KotlinLogging.logger {}

    fun runGameLoop(gameId: UUID) {
        logger.debug("Initializing Game-Loop for game gameId={}", gameId)
        var currentRoundId: UUID
        var currentGame: Game
        var currentTimeFrame: TimeFrame

        try {
            currentGame = gameRepository.findById(gameId).get()
            currentRoundId = currentGame.getCurrentRound()!!.getRoundId()
            logger.debug("Initial Round has roundId={}", currentRoundId)
            logger.trace("Current Game is:")
            logger.trace { currentGame.toString() }

        } catch (e: Exception) {
            logger.error("Failed to obtain currentRoundId. Either the Game has not been started or does not exist with gameId={}", gameId)
            logger.error{ e.message }
            throw GameStateException("Unable to start Game-Loop. No active round exists for gameId=$gameId")
        }

        logger.info("Starting Game-Loop for gameId={}...", gameId)
        while (true) {
            currentTimeFrame = TimeFrame(currentGame)
            startRound(currentGame.getCurrentRound()!!)
                logger.info("Accepting Commands. Waiting for {}s ...", (currentTimeFrame.getCommandInputTimeFrameInMS().toDouble() / 1000))
                Thread.sleep(currentTimeFrame.getCommandInputTimeFrameInMS())

            executeCommandsInOrder(currentRoundId)
                logger.info("Commands executed. Waiting for {}s ...", (currentTimeFrame.getExecutionTimeFrameInMS().toDouble() / 1000))
                Thread.sleep(currentTimeFrame.getExecutionTimeFrameInMS())

            endRound(currentRoundId)

            try {
                val (g, r) = makeNextRound(gameId)
                currentGame = g
                currentRoundId = r
            } catch (e: Exception) {
                logger.debug("Failed to start next Round: {}", e.message)
                logger.debug("Exiting Game-Loop.")
                break
            }
        }
        endGame(gameId)
        logger.info("Game ended. Game-Loop closed.")
    }

    // PUBLIC FOR DEBUG PURPOSES ONLY
    fun startRound(round: Round) {
        val transactionId = UUID.randomUUID()
        logger.trace("Start-Round transactionId={}", transactionId)

        val roundEvent: RoundStatusEvent = roundStatusEventBuilder.makeRoundStatusEvent(
            transactionId, round.getRoundId(), round.getRoundNumber(), RoundStatus.COMMAND_INPUT_STARTED
        )
        logger.debug("Handing RoundStatusEvent off to EventPublisher & -Store. [transactionId={}, roundNumber={}, roundStatus={}]",
            transactionId, roundEvent.roundId, roundEvent.roundStatus)
        eventStoreService.storeEvent(roundEvent)
        eventPublisherService.publishEvent(roundEvent)

        logger.info("Round {} started.", round.getRoundNumber())
    }

    // PUBLIC FOR DEBUG PURPOSES ONLY
    fun executeCommandsInOrder(roundId: UUID) {
        logger.info("Dispatching commands in order...")
        roundService.endCommandInputs(roundId)
        roundService.deliverBlockingCommands(roundId)
        roundService.deliverTradingCommands(roundId)
        roundService.deliverMovementCommands(roundId)
        roundService.deliverBattleCommands(roundId)
        roundService.deliverMiningCommands(roundId)
        roundService.deliverRegeneratingCommands(roundId)
        logger.info("Command dispatching completed.")
    }

    // PUBLIC FOR DEBUG PURPOSES ONLY
    fun endRound(roundId: UUID) {
        roundService.endRound(roundId)
    }

    // PUBLIC FOR DEBUG PURPOSES ONLY
    fun makeNextRound(gameId: UUID): Pair<Game, UUID> {
        logger.debug("Attempting to start next round...")
        val game: Game = fetchGame(gameId)
        game.startNewRound()
        val roundId = game.getCurrentRound()!!.getRoundId()

        gameRepository.save(game)
        logger.debug("New Round successfully started and saved. [roundId={}, roundNumber={}",
            roundId, game.getCurrentRound()!!.getRoundNumber())
        return Pair(game, roundId)
    }

    // PUBLIC FOR DEBUG PURPOSES ONLY
    fun endGame(gameId: UUID) {
        logger.debug("Ending Game...")

        val transactionId = UUID.randomUUID()
        val game: Game = fetchGame(gameId)
        logger.trace("End-Round transactionId={}", transactionId)
        logger.trace("Fetched Game is:")
        logger.trace{ game.toString() }

        game.endGame()
        gameRepository.save(game)
        logger.debug("Ended Game saved.")
        logger.trace{ game.toString() }

        val gameEndedEvent: GameStatusEvent = gameStatusEventBuilder.makeGameStatusEvent(transactionId, game.getGameId(), GameStatus.GAME_FINISHED)
        logger.debug("Handing GameStatusEvent off to EventPublisher & -Store. [transactionId={}, gameId={}, gameStatus={}]",
            transactionId, gameId, GameStatus.GAME_FINISHED)
        eventStoreService.storeEvent(gameEndedEvent)
        eventPublisherService.publishEvent(gameEndedEvent)
    }

    private fun fetchGame(gameId: UUID): Game {
        try {
            return gameRepository.findById(gameId).get()
        } catch (e: Exception) {
            logger.error("Game-Loop terminated. Game was not found even though it should exist. gameId={}", gameId)
            throw GameLoopException("Game-Loop terminated. Game was not found even though it should exist. $gameId")
        }
    }
}