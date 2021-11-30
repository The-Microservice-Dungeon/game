package microservice.dungeon.game.aggregates.commands.controller

import microservice.dungeon.game.aggregates.commands.domain.Command
import microservice.dungeon.game.aggregates.commands.dtos.CommandDTO
import microservice.dungeon.game.aggregates.commands.services.CommandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

//TODO: Errors

@RestController
@RequestMapping("/api")
class CommandController(@Autowired private val commandService: CommandService) {

    @GetMapping("/commands")
    fun getAllCommands(): List<Command> = commandService.getAllCommands()

    @PostMapping("/command/save")
    fun createNewCommand(@ModelAttribute command: CommandDTO): ResponseEntity<UUID> {
        return ResponseEntity.ok(commandService.save(command))
    }

    @GetMapping("/commands/search")
    fun getCommandById(@RequestParam("commandId") commandId: UUID): ResponseEntity<Command> {
        return commandService.getCommandById(commandId).map { command ->
            ResponseEntity.ok(command)
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/commands/delete")
    fun deleteCommandById(@RequestParam("commandId") commandId: UUID): ResponseEntity<Void> {

        return commandService.deleteCommandById(commandId).map { _ ->
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())
    }
}