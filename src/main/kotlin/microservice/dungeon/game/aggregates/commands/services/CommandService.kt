package microservice.dungeon.game.aggregates.commands.services

import microservice.dungeon.game.aggregates.commands.domain.Command
import microservice.dungeon.game.aggregates.commands.dtos.CommandDTO
import microservice.dungeon.game.aggregates.commands.repository.CommandRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommandService(private val commandRepository: CommandRepository) {

    fun getAllCommands(): List<Command> =
        commandRepository.findAll() //TODO possibly have to add a second repo to save the list of commands and their respective round number

    fun save(dto: CommandDTO): UUID {
        val command: Command = Command.fromDTO(dto)
        commandRepository.save(command)
        return command.transactionId
    }

    fun getCommandById(commandId: UUID): Optional<Command> = commandRepository.findById(commandId)

    fun deleteCommandById(commandId: UUID): Optional<Unit> {
        return commandRepository.findById(commandId).map { command ->
            commandRepository.delete(command)
        }
    }
}