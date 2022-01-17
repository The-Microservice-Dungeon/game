package microservice.dungeon.game.aggregates.command.controller

import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.dtos.CommandDTO
import microservice.dungeon.game.aggregates.command.dtos.CommandResponseDTO
import microservice.dungeon.game.aggregates.command.services.CommandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class CommandController @Autowired constructor(private val commandService: CommandService) {

    @GetMapping("/commands", produces = ["application/json"])
    fun getAllRoundCommands(@RequestParam gameId: UUID, @RequestParam roundNumber: Int): ResponseEntity<List<Command>> {
        try {
            val roundCommands = commandService.getAllRoundCommands(gameId, roundNumber)
            if (roundCommands != null) {
                return ResponseEntity(roundCommands, HttpStatus.OK)
            } else {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "roundNumber not found")
            }
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString())
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString())
        }
    }

    @PostMapping("/commands", consumes = ["application/json"], produces = ["application/json"])
    fun createNewCommand(@RequestBody command: CommandDTO): ResponseEntity<CommandResponseDTO> {
        try {
            val commandId = commandService.save(command)
            return ResponseEntity(CommandResponseDTO(commandId), HttpStatus.CREATED)
        } catch (e: IllegalAccessException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, e.message)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}