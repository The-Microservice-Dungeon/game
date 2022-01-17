package microservice.dungeon.game.aggregates.command.services

import microservice.dungeon.game.aggregates.command.controller.dto.CommandRequestDto
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandArgumentException
import microservice.dungeon.game.aggregates.command.domain.CommandPayload
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.game.domain.Game
import microservice.dungeon.game.aggregates.game.domain.GameNotFoundException
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

    @Throws(PlayerNotFoundException::class, GameNotFoundException::class, GameStateException::class, CommandArgumentException::class)
    fun createNewCommand(gameId: UUID, playerToken: UUID, robotId: UUID?, commandType: CommandType, commandRequestDTO: CommandRequestDto): UUID {
        val transactionId: UUID = UUID.randomUUID()
        val player: Player
        val game: Game
        val round: Round

        try {
            player = playerRepository.findByPlayerToken(playerToken).get()
        } catch (e: Exception) {
            logger.warn("TODO")
            throw PlayerNotFoundException("TODO")
        }
        try {
            game = gameRepository.findById(gameId).get()
        } catch (e: Exception) {
            logger.warn("TODO")
            throw GameNotFoundException("TODO")
        }
        try {
            round = game.getCurrentRound()!!
        } catch (e: Exception) {
            logger.warn("TODO")
            throw GameStateException("TODO")
        }
        val robot = try {
            robotRepository.findById(robotId!!).get()
        } catch (e: Exception) {
            null
        }

        // TODO COMMAND
            // TODO PREVENT DUPLICATES
            // TODO ONLY VALID COMMANDS
        // TODO SAVE
        val command: Command = Command(
            commandId = transactionId,
            round = round,
            player = player,
            robot = robot,
            commandType = commandType,
            commandPayload = CommandPayload(
                planetId = commandRequestDTO.commandObject.planetId,
                targetId = commandRequestDTO.commandObject.targetId,
                itemName = commandRequestDTO.commandObject.itemName,
                itemQuantity = commandRequestDTO.commandObject.itemQuantity
            )
        )
        commandRepository.save(command)

        logger.info("TODO")
        return transactionId
    }

    fun save(dto: CommandRequestDto): UUID {
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
        return UUID.randomUUID()
    }
}