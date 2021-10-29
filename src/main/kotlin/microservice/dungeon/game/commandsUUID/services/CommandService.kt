package microservice.dungeon.game.commandsUUID.services

import microservice.dungeon.game.commandsUUID.model.Command
import microservice.dungeon.game.commandsUUID.repository.CommandRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommandService(private val commandRepository: CommandRepository) {

    fun getAllCommands(): List<Command> = commandRepository.findAll()

    fun save(command: Command): Command = commandRepository.save(command)

    fun getCommandById(commandId: Int): Optional<Command> = commandRepository.findById(commandId)

    fun updateCommandById(commandId: Int, newCommand: Command): Optional<Command> {
        return commandRepository.findById(commandId).map { existingCommand ->
            val updatedCommand: Command = existingCommand
                .copy(
                    commandBody = newCommand.commandBody,
                    commandType = newCommand.commandType,
                    item = newCommand.item,
                    playerId = newCommand.playerId,
                    robotId = newCommand.robotId
                )
            commandRepository.save(updatedCommand)
        }
    }

    fun deleteCommandById(commandId: Int): Optional<Unit> {
        return commandRepository.findById(commandId).map { command ->
            commandRepository.delete(command)
        }
    }
}