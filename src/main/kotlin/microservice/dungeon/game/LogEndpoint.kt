package microservice.dungeon.game

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import java.util.stream.Stream

@Component
@Endpoint(id = "logs")
class LogEndpoint @Autowired constructor(
    @Value("\${logging.file.path}") private val logFilePath: String
) {
    // GET /actuator/logs
    @ReadOperation(produces = [MediaType.TEXT_PLAIN_VALUE])
    fun logs(): String {
        val path: Path = Paths.get("$logFilePath/spring.log")
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