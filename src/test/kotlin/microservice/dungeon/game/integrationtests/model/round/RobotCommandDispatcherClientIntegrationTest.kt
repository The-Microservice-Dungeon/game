package microservice.dungeon.game.integrationtests.model.round

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.command.dtos.BlockCommandDTO
import microservice.dungeon.game.aggregates.command.dtos.MovementCommandDTO
import microservice.dungeon.game.aggregates.command.dtos.RobotCommandWrapperDTO
import microservice.dungeon.game.aggregates.command.dtos.UseItemMovementCommandDTO
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.util.*

class RobotCommandDispatcherClientIntegrationTest {
    private var mockWebServer: MockWebServer? = null
    private var robotCommandDispatcherClient: RobotCommandDispatcherClient? = null

    private val objectMapper = ObjectMapper().findAndRegisterModules()


    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer!!.start()
        robotCommandDispatcherClient = RobotCommandDispatcherClient(mockWebServer!!.url("/").toString())
    }


    @Test
    fun shouldAllowToSendBlockingCommands() {
        //given
        val inputCommands = listOf(
            BlockCommandDTO(UUID.randomUUID(), UUID.randomUUID()),
            BlockCommandDTO(UUID.randomUUID(), UUID.randomUUID())
        )
        val mockResponse = MockResponse()
            .setResponseCode(202)
        mockWebServer!!.enqueue(mockResponse)

        //when
        robotCommandDispatcherClient!!.sendBlockingCommands(inputCommands)

        //and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRobotCommandWrapperDTO = objectMapper.readValue(
            recordedRequest.body.readUtf8(),
            RobotCommandWrapperDTO::class.java
        )
        val recordedBlockingCommandDTOs: List<BlockCommandDTO> = recordedRobotCommandWrapperDTO.commands.map {
                x -> BlockCommandDTO.fromString(x)
        }

        //then
        assertThat(recordedRequest.method)
            .isEqualTo("POST")
        assertThat(recordedRequest.path)
            .isEqualTo("/commands")
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        //and
        assertThat(recordedBlockingCommandDTOs)
            .isEqualTo(inputCommands)
    }

    @Test
    fun shouldAllowToSendMovementItemUseCommands() {
        //given
        val inputCommands = listOf(
            UseItemMovementCommandDTO(UUID.randomUUID(), "ANY_NAME", UUID.randomUUID()),
            UseItemMovementCommandDTO(UUID.randomUUID(), "ANY_NAME", UUID.randomUUID())
        )
        val mockResponse = MockResponse()
            .setResponseCode(202)
        mockWebServer!!.enqueue(mockResponse)

        //when
        robotCommandDispatcherClient!!.sendMovementItemUseCommands(inputCommands)

        //and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRobotCommandWrapperDTO = objectMapper.readValue(
            recordedRequest.body.readUtf8(),
            RobotCommandWrapperDTO::class.java
        )
        val recordedMovementItemUseCommandDTOs: List<UseItemMovementCommandDTO> = recordedRobotCommandWrapperDTO.commands.map {
                x -> UseItemMovementCommandDTO.fromString(x)
        }

        //then
        assertThat(recordedRequest.method)
            .isEqualTo("POST")
        assertThat(recordedRequest.path)
            .isEqualTo("/commands")
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        //and
        assertThat(recordedMovementItemUseCommandDTOs)
            .isEqualTo(inputCommands)
    }

    @Test
    fun shouldAllowToSendMovementCommands() {
        //given
        val inputCommands = listOf(
            MovementCommandDTO(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()),
            MovementCommandDTO(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
        )
        val mockResponse = MockResponse()
            .setResponseCode(202)
        mockWebServer!!.enqueue(mockResponse)

        //when
        robotCommandDispatcherClient!!.sendMovementCommands(inputCommands)

        //and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRobotCommandWrapperDTO = objectMapper.readValue(
            recordedRequest.body.readUtf8(),
            RobotCommandWrapperDTO::class.java
        )
        val recordedMovementCommandDTOs: List<MovementCommandDTO> = recordedRobotCommandWrapperDTO.commands.map {
                x -> MovementCommandDTO.fromString(x)
        }

        //then
        assertThat(recordedRequest.method)
            .isEqualTo("POST")
        assertThat(recordedRequest.path)
            .isEqualTo("/commands")
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        //and
        assertThat(recordedMovementCommandDTOs)
            .isEqualTo(inputCommands)
    }
}