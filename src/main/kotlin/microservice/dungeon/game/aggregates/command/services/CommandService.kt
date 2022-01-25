package microservice.dungeon.game.aggregates.command.services

import microservice.dungeon.game.aggregates.command.controller.dto.CommandRequestDto
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandArgumentException
import microservice.dungeon.game.aggregates.command.domain.CommandPayload
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameNotFoundException
import microservice.dungeon.game.aggregates.game.domain.GameParticipationException
import microservice.dungeon.game.aggregates.game.domain.GameStateException
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.player.domain.Player
import microservice.dungeon.game.aggregates.player.domain.PlayerNotFoundException
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import microservice.dungeon.game.aggregates.round.domain.Round
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class CommandService @Autowired constructor(
    private val commandRepository: CommandRepository,
    private val robotRepository: RobotRepository,
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @Transactional
    @Throws(PlayerNotFoundException::class, GameNotFoundException::class, GameStateException::class, CommandArgumentException::class)
    fun createNewCommand(gameId: UUID, playerToken: UUID, robotId: UUID?, commandType: CommandType, commandRequestDTO: CommandRequestDto): UUID {
        val transactionId: UUID

        val player: Player
        try {
            player = playerRepository.findByPlayerToken(playerToken).get()
        } catch (e: Exception) {
            logger.debug("Command-Creation failed. Player not found. [playerToken=XXX]")
            throw PlayerNotFoundException("Player not found.")
        }
        val game: Game
        try {
            game = gameRepository.findById(gameId).get()
        } catch (e: Exception) {
            logger.debug("Command-Creation failed. Game not found. [gameId={}, playerName={}]", gameId, player.getUserName())
            throw GameNotFoundException("Game not found.")
        }
        val round: Round
        try {
            round = game.getCurrentRound()!!
        } catch (e: Exception) {
            logger.debug("Command-Creation failed. Round not found. [gameId={}, gameStatus={}, playerName={}]",
                gameId, game.getGameStatus(), player.getUserName())
            logger.trace { game.toString() }
            throw GameStateException("Game not in a state to accept commands. [gameStatus=${game.getGameStatus()}]")
        }
        if (!game.isParticipating(player)) {
            logger.debug("Command-Creation failed. Player is not participating in the game. [gameId={}, playerName={}]",
                gameId, player.getUserName())
            throw GameParticipationException("Player '${player.getUserName()}' is not participating in the game.")
        }
        val robot = try {
            robotRepository.findById(robotId!!).get()
        } catch (e: Exception) {
            logger.debug("Robot not found. [commandType={}, playerName={}, robotId={}]", commandType, player.getUserName(), robotId)
            null
        }

        val newCommand = Command.makeCommandFromDto(
            round = round,
            player = player,
            robot = robot,
            commandType = commandType,
            dto = commandRequestDTO
        )
        transactionId = newCommand.getCommandId()

        commandRepository.deleteCommandsByRoundAndPlayerAndRobot(round, player, robot)
        logger.debug("Previous Player-Commands for Round {}, Player {} and Robot deleted.", round.getRoundNumber(), player.getUserName())

        commandRepository.save(newCommand)
        logger.debug("New Command saved. [transactionId={}, playerName={}]", newCommand.getCommandId())
        logger.info("New Command successfully created. [playerName={}, commandType={}, roundNumber={}]",
            player.getUserName(), commandType, round.getRoundNumber())

        return transactionId
    }

//    fun save(dto: CommandRequestDto): UUID {
//        val currentRoundNumber = 0 //TODO("REPLACE") gameRepository.findById(dto.gameId).get().getCurrentRoundCount()
//
//        val player = playerRepository.findByPlayerToken(dto.playerToken)
//
//        if (player.isEmpty) throw IllegalAccessException("Player could not be found.")
//
//        var command: Command = Command.fromDto(dto, currentRoundNumber, player.get().getPlayerId())
//
//        //TODO Test ;)
//        if (!robotRepository.findAll()
//                .any { r -> r.getRobotId() == command.robotId && r.getPlayerId() == command.playerId && r.getRobotStatus() == RobotStatus.ACTIVE }
//        ) {
//            throw IllegalAccessException("Player is not allowed to send commands to this robot or robot is inactive.")
//        }
//
//        val prevCommands = commandRepository.findAll()
//        prevCommands.removeIf { c -> c.robotId != command.robotId && c.roundNumber == currentRoundNumber }
//
//        if (prevCommands.isNotEmpty()) {
//            commandRepository.deleteAll(prevCommands)
//        }
//        command = commandRepository.save(command)
//        return command.commandId
//        return UUID.randomUUID()
//    }
}