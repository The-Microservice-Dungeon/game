package microservice.dungeon.game.aggregates.commands.services

import microservice.dungeon.game.aggregates.commands.domain.Command
import microservice.dungeon.game.aggregates.commands.domain.RoundCommands
import microservice.dungeon.game.aggregates.commands.dtos.CommandDTO
import microservice.dungeon.game.aggregates.commands.repositories.CommandRepository
import microservice.dungeon.game.aggregates.commands.repositories.RoundCommandsRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommandService(
    private val commandRepository: CommandRepository,
    private val roundCommandsRepository: RoundCommandsRepository
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
        command = commandRepository.save(command)
        return command.transactionId
    }

    fun sendCommands() {
        //TODO send commands in their phase
    }

    fun saveRoundCommands() {
        roundCommandsRepository.save(RoundCommands(commandRepository.findAll(), 0)) //TODO get current roundNumber
    }
}