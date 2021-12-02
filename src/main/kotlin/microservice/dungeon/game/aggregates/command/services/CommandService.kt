package microservice.dungeon.game.aggregates.command.services

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.RoundCommands
import microservice.dungeon.game.aggregates.command.dtos.CommandDTO
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.command.repositories.RoundCommandsRepository
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

    fun saveRoundCommands(roundNumber: Int) {
        roundCommandsRepository.save(RoundCommands(commandRepository.findAll(), roundNumber))
    }
}