package microservice.dungeon.game.aggregates.game.servives

import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameLoopException
import microservice.dungeon.game.aggregates.game.domain.GameStateException
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.services.RoundService
import mu.KotlinLogging
import java.util.*


class GameLoop(
    private val gameRepository: GameRepository,
    private val roundService: RoundService
) {
    private val logger = KotlinLogging.logger {}

    fun runGameLoop(gameId: UUID) {
        var currentRoundId: UUID

        try {
            currentRoundId = gameRepository.findById(gameId).get()
                .getCurrentRound()!!
                .getRoundId()

        } catch (e: Exception) {
            logger.error("Failed to obtain currentRoundId. Either the game has not started or does not exist.")
            logger.error("[gameId=$gameId]")
            throw GameStateException("Unable to start game-loop. No active round exists.")
        }

        while (true) {
            startRound()
                Thread.sleep(5000)

            executeCommandsInOrder(currentRoundId)
                Thread.sleep(5000)

            endRound(currentRoundId)

            try {
                currentRoundId = makeNextRound(gameId)
            } catch (e: Exception) {
                break
            }
        }
        endGame(gameId)
        logger.info("Game has ended.")
    }

    // DEBUG ONLY: EXTERNAL USE NOT PERMITTED
    private fun startRound() {
        // TODO Publish RoundStartedEvent
        logger.info("Round started. Accepting commands...")
    }

    // DEBUG ONLY: EXTERNAL USE NOT PERMITTED
    fun executeCommandsInOrder(roundId: UUID) {
        logger.info("Dispatching commands...")
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
    fun makeNextRound(gameId: UUID): UUID {
        val game: Game = fetchGame(gameId)
        game.startNewRound()
        val roundId = game.getCurrentRound()!!.getRoundId()

        gameRepository.save(game)
        logger.debug("Saved game with next round.")
        return roundId
    }

    // DEBUG ONLY: EXTERNAL USE NOT PERMITTED
    fun endGame(gameId: UUID) {
        val game: Game = fetchGame(gameId)

        game.endGame()
        gameRepository.save(game)

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