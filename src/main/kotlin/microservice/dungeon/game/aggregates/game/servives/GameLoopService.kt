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
        var currentRoundId: UUID
        var currentGame: Game
        var currentTimeFrame: TimeFrame

        try {
            currentGame = gameRepository.findById(gameId).get()
            currentRoundId = currentGame.getCurrentRound()!!.getRoundId()

        } catch (e: Exception) {
            logger.error("Failed to obtain currentRoundId. Either the game has not started or does not exist.")
            logger.error("[gameId=$gameId]")
            throw GameStateException("Unable to start game-loop. No active round exists.")
        }

        while (true) {
            currentTimeFrame = TimeFrame(currentGame)
            startRound(currentGame.getCurrentRound()!!)
                logger.debug("Waiting for ${currentTimeFrame.getCommandInputTimeFrameInMS().toDouble() / 1000}s ...")
                Thread.sleep(currentTimeFrame.getCommandInputTimeFrameInMS())

            executeCommandsInOrder(currentRoundId)
                logger.debug("Waiting for ${currentTimeFrame.getExecutionTimeFrameInMS().toDouble() / 1000}s ...")
                Thread.sleep(currentTimeFrame.getExecutionTimeFrameInMS())

            endRound(currentRoundId)

            try {
                val (g, r) = makeNextRound(gameId)
                currentGame = g
                currentRoundId = r
            } catch (e: Exception) {
                break
            }
        }
        endGame(gameId)
        logger.info("Game has ended.")
    }

    // DEBUG ONLY: EXTERNAL USE NOT PERMITTED
    fun startRound(round: Round) {
        val transactionId = UUID.randomUUID()
        val roundEvent: RoundStatusEvent = roundStatusEventBuilder.makeRoundStatusEvent(
            transactionId, round.getRoundId(), round.getRoundNumber(), RoundStatus.COMMAND_INPUT_STARTED
        )
        eventStoreService.storeEvent(roundEvent)
        eventPublisherService.publishEvent(roundEvent)
        logger.debug("RoundStatusEvent handed off to publisher & store. [transactionId=$transactionId, roundNumber=${roundEvent.roundId}, roundStatus=${roundEvent.roundStatus}]")

        logger.info("Round started. Accepting commands ...")
    }

    // DEBUG ONLY: EXTERNAL USE NOT PERMITTED
    fun executeCommandsInOrder(roundId: UUID) {
        logger.info("Dispatching commands ...")
        roundService.endCommandInputs(roundId)
        roundService.deliverBlockingCommands(roundId)
        roundService.deliverTradingCommands(roundId)
        roundService.deliverMovementCommands(roundId)
        roundService.deliverBattleCommands(roundId)
        roundService.deliverMiningCommands(roundId)
        roundService.deliverRegeneratingCommands(roundId)
        logger.info("Command dispatching completed.")
    }

    // DEBUG ONLY: EXTERNAL  USE NOT PERMITTED
    fun endRound(roundId: UUID) {
        roundService.endRound(roundId)
        logger.info("Round has ended.")
    }

    // DEBUG ONLY: EXTERNAL USE NOT PERMITTED
    fun makeNextRound(gameId: UUID): Pair<Game, UUID> {
        val game: Game = fetchGame(gameId)
        game.startNewRound()
        val roundId = game.getCurrentRound()!!.getRoundId()

        gameRepository.save(game)
        logger.debug("Saved game with next round.")
        return Pair(game, roundId)
    }

    // DEBUG ONLY: EXTERNAL USE NOT PERMITTED
    fun endGame(gameId: UUID) {
        val transactionId = UUID.randomUUID()
        val game: Game = fetchGame(gameId)

        game.endGame()
        gameRepository.save(game)

        val gameEndedEvent: GameStatusEvent = gameStatusEventBuilder.makeGameStatusEvent(transactionId, game.getGameId(), GameStatus.GAME_FINISHED)
        eventStoreService.storeEvent(gameEndedEvent)
        eventPublisherService.publishEvent(gameEndedEvent)
        logger.debug("GameStatusEvent handed off to publisher & store. [transactionId=$transactionId, gameId=${gameId}, gameStatus=${GameStatus.GAME_FINISHED}]")

        logger.debug("Saved finished game.")
        logger.trace(game.toString())
    }

    private fun fetchGame(gameId: UUID): Game {
        try {
            return gameRepository.findById(gameId).get()
        } catch (e: Exception) {
            logger.error("Game-Loop terminated. Game was not found even though it should exist. $gameId")
            throw GameLoopException("Game-Loop terminated. Game was not found even though it should exist. $gameId")
        }
    }
}