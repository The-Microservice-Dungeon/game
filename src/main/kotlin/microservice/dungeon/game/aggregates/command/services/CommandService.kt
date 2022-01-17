package microservice.dungeon.game.aggregates.command.services

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.dtos.CommandDTO
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
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

    fun save(dto: CommandDTO): UUID {
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