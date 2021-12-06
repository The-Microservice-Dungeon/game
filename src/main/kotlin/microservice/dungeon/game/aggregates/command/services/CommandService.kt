package microservice.dungeon.game.aggregates.command.services

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.dtos.CommandDto
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.game.repositories.GameRepository
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommandService @Autowired constructor(
    private val commandRepository: CommandRepository,
    private val robotRepository: RobotRepository,
    private val gameRepository: GameRepository
) {

    fun getAllRoundCommands(gameId: UUID, roundNumber: Int): List<Command>? {
        val currentRoundNumber = gameRepository.findById(gameId).get().getCurrentRoundCount()
        val roundCommands = commandRepository.findAll()

        if (currentRoundNumber >= roundNumber) {
            throw IllegalAccessException("Current Round may not be requested")
        }

        roundCommands.removeIf { c -> c.roundNumber != roundNumber }
        if (roundCommands.isNotEmpty()) {
            return roundCommands
        } else {
            throw IllegalArgumentException("Round could not be found")
        }
    }

    fun save(dto: CommandDto): UUID {
        val currentRoundNumber = gameRepository.findById(dto.gameId).get().getCurrentRoundCount()

        var command: Command = Command.fromDto(dto, currentRoundNumber)

        //TODO Test ;)
        if (!robotRepository.findAll()
                .any { r -> r.getRobotId() == command.robotId && r.getPlayerId() == command.playerId }
        ) {
            throw IllegalAccessException("Player is not allowed to send commands to this robot.")
        }

        val prevCommands = commandRepository.findAll()
        prevCommands.removeIf { c -> c.robotId != command.robotId && c.roundNumber == currentRoundNumber }
        if (prevCommands.isNotEmpty()) {
            commandRepository.deleteAll(prevCommands)
        }
        command = commandRepository.save(command)
        return command.transactionId
    }
}