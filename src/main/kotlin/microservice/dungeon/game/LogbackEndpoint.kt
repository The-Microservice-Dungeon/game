package microservice.dungeon.game

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import java.util.stream.Stream

@RestController
class LogbackEndpoint @Autowired constructor(
    @Value("\${logging.file.path}") private val logFilePath: String
) {
    @GetMapping("/logs/info", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getInfoLog(): String {
        return  getLogOutput("$logFilePath/info.log")
    }

    @GetMapping("/logs/trace", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getTraceLog(): String {
        return  getLogOutput("$logFilePath/trace.log")
    }

    @GetMapping("/logs/error", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getErrorLog(): String {
        return  getLogOutput("$logFilePath/error.log")
    }

    @GetMapping("/logs/game-info", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getGameInfoLog(): String {
        return getLogOutput("$logFilePath/game-info.log")
    }

    @GetMapping("/logs/game-trace", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getGameTraceLog(): String {
        return getLogOutput("$logFilePath/game-trace.log")
    }

    @DeleteMapping("/logs")
    fun purgeLogs(): ResponseEntity<HttpStatus> {
        return try {
            Files.deleteIfExists(Path.of("./logs"));
            ResponseEntity(HttpStatus.OK);
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.OK)
        }
    }


    private fun getLogOutput(logPath: String): String {
        val path: Path = Paths.get(logPath)
        try {
            val lines: Stream<String> = Files.lines(path)
            val data: String = lines.collect(Collectors.joining("\n"))
            lines.close()
            return data
        } catch (ignored: IOException) {

        }
        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}