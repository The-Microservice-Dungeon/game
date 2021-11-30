package microservice.dungeon.game.aggregates.commands.controller

import microservice.dungeon.game.aggregates.commands.domain.Command
import microservice.dungeon.game.aggregates.commands.dtos.CommandDTO
import microservice.dungeon.game.aggregates.commands.services.CommandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api")
class CommandController(@Autowired private val commandService: CommandService) {

    @GetMapping("/commands", consumes = ["application/json"], produces = ["application/json"])
    fun getAllRoundCommands(@ModelAttribute roundNumber: Number): ResponseEntity<List<Command>> {
        try {
            val roundCommands = commandService.getAllRoundCommands(roundNumber)
            if (roundCommands != null) {
                return ResponseEntity(roundCommands, HttpStatus.OK)
            } else {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "roundNumber not found")
            }
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString())
        }
    }

    @PostMapping("/commands", consumes = ["application/json"], produces = ["application/json"])
    fun createNewCommand(@ModelAttribute command: CommandDTO): ResponseEntity<UUID> =
        ResponseEntity(commandService.save(command), HttpStatus.CREATED)
}