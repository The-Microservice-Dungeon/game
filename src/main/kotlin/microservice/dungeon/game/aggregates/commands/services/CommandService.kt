package microservice.dungeon.game.aggregates.commands.services

import microservice.dungeon.game.aggregates.commands.domain.Command
import microservice.dungeon.game.aggregates.commands.dtos.CommandDTO
import microservice.dungeon.game.aggregates.commands.repositories.CommandRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommandService(
    private val commandRepository: CommandRepository,
    private val pastCommandRepository: CommandRepository
) {

    fun getAllRoundCommands(roundNumber: Number): List<Command>? {
        return pastCommandRepository.findAll()
    }

    fun save(dto: CommandDTO): UUID {
        var command: Command = Command.fromDTO(dto)
        command = commandRepository.save(command)
        return command.transactionId
    }

    fun sendCommands() {
        //TODO send commands in their phase
        //TODO save the list of commands in pastCommandRepository
    }
}