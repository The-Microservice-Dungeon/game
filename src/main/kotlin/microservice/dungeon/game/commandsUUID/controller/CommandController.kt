package microservice.dungeon.game.commandsUUID.controller

import microservice.dungeon.game.commandsUUID.model.Command
import microservice.dungeon.game.commandsUUID.services.CommandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class CommandController(@Autowired private val commandService: CommandService) {

    @GetMapping("/commands")
    fun getAllCommands(): List<Command> = commandService.getAllCommands()

    @PostMapping("/command/save")
    fun createNewCommand(@ModelAttribute command: Command): Command = commandService.save(command)

    @GetMapping("/commands/search")
    fun getCommandById(@RequestParam("commandId") commandId: Int): ResponseEntity<Command> {
        return commandService.getCommandById(commandId).map { command ->
            ResponseEntity.ok(command)
        }.orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/commands/{id}")
    fun updateCommandById(
        @PathVariable(value = "id") commandId: Int,
        @Valid @RequestBody newCommand: Command
    ): ResponseEntity<Command> {

        val updatedCommand: Optional<Command> = commandService.updateCommandById(commandId, newCommand)
        return updatedCommand.map { updated ->
            ResponseEntity.ok().body(updated)
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/commands/delete")
    fun deleteCommandById(@RequestParam("commandId") commandId: Int): ResponseEntity<Void> {

        return commandService.deleteCommandById(commandId).map { _ ->
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())

    }
}