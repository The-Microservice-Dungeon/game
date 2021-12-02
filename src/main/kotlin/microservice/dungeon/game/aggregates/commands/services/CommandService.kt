package microservice.dungeon.game.aggregates.commands.services

import microservice.dungeon.game.aggregates.commands.domain.Command
import microservice.dungeon.game.aggregates.commands.domain.RoundCommands
import microservice.dungeon.game.aggregates.commands.dtos.CommandDTO
import microservice.dungeon.game.aggregates.commands.repositories.CommandRepository
import microservice.dungeon.game.aggregates.commands.repositories.RoundCommandsRepository
import microservice.dungeon.game.aggregates.robot.repositories.RobotRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommandService @Autowired constructor(
    private val commandRepository: CommandRepository,
    private val roundCommandsRepository: RoundCommandsRepository,
    private val robotRepository: RobotRepository
) {

    fun getAllRoundCommands(roundNumber: Number): List<Command>? {
        val roundCommands = roundCommandsRepository.findById(roundNumber.toInt())
        if (roundCommands.isPresent) {
            return roundCommands.get().list
        } else {
            throw IllegalArgumentException("Round could not be found")
        }
    }

    fun save(dto: CommandDTO): UUID {
        var command: Command = Command.fromDTO(dto)

        //TODO Test ;)
        if (!robotRepository.findAll()
                .any { r -> r.getRobotId() == command.robotId && r.getPlayerId() == command.playerId }
        ) {
            throw IllegalAccessException("Player is not allowed to send commands to this robot.")
        }

        val prevCommands = commandRepository.findByRobotIdIn(command.robotId)
        if (prevCommands.isNotEmpty()) {
            commandRepository.deleteAll(prevCommands)
        }
        command = commandRepository.save(command)
        return command.transactionId
    }

    fun sendCommands() {
        //TODO divide the commands up by their phase
    }

    fun saveRoundCommands() {
        roundCommandsRepository.save(RoundCommands(commandRepository.findAll(), 0)) //TODO get current roundNumber
    }
}