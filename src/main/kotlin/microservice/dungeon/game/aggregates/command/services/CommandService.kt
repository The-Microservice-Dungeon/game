package microservice.dungeon.game.aggregates.command.services

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.dtos.CommandDTO
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.core.EntityNotFoundException
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.player.repository.PlayerRepository
import microservice.dungeon.game.aggregates.robot.domain.RobotStatus
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
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

    fun getAllRoundCommands(gameId: UUID, roundNumber: Int): List<Command>? {
        val currentRoundNumber = gameRepository.findById(gameId).get().getCurrentRoundCount()
        val roundCommands = commandRepository.findAll()

        if (currentRoundNumber == roundNumber) {
            throw IllegalAccessException("Current round may not be requested.")
        }
        if (currentRoundNumber > roundNumber) {
            throw IllegalArgumentException("Future rounds may not be requested.")
        }

        roundCommands.removeIf { c -> c.roundNumber != roundNumber }
        if (roundCommands.isNotEmpty()) {
            return roundCommands
        } else {
            throw IllegalArgumentException("No commands could be found.")
        }
    }

    fun save(dto: CommandDTO): UUID {
        val currentRoundNumber = gameRepository.findById(dto.gameId).get().getCurrentRoundCount()

        val player = playerRepository.findByPlayerToken(dto.playerToken)

        if (player.isEmpty) throw EntityNotFoundException("Player could not be found.")

        var command: Command = Command.fromDto(dto, currentRoundNumber, player.get().playerId)

        //TODO Test ;)
        if (dto.robotId != null) {
            if (!robotRepository.findAll()
                    .any { r -> r.getRobotId() == command.robotId && r.getPlayerId() == command.playerId && r.getRobotStatus() == RobotStatus.ACTIVE }
            ) {
                throw IllegalAccessException("Player is not allowed to send commands to this robot or robot is inactive.")
            }

            val prevCommands = commandRepository.findAll()
            prevCommands.removeIf { c -> c.robotId != command.robotId && c.roundNumber == currentRoundNumber }

            if (prevCommands.isNotEmpty()) {
                commandRepository.deleteAll(prevCommands)
            }
        }
        command = commandRepository.save(command)
        return command.transactionId
    }
}