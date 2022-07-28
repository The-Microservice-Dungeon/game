package microservice.dungeon.game.integrationtests.model.round.web

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.round.web.RobotCommandDispatcherClient
import microservice.dungeon.game.aggregates.round.web.dto.*
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


  /*  @Test
    fun shouldAllowToSendBlockingCommands() {
        //given
        val inputCommands = listOf(
            BlockCommandDto(UUID.randomUUID(), UUID.randomUUID()),
            BlockCommandDto(UUID.randomUUID(), UUID.randomUUID())
        )
        val mockResponse = MockResponse()
            .setResponseCode(202)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody("Command batch accepted");
        mockWebServer!!.enqueue(mockResponse)

        //when
        robotCommandDispatcherClient!!.sendBlockingCommands(inputCommands)

        //and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRobotCommandWrapperDTO = objectMapper.readValue(
            recordedRequest.body.readUtf8(),
            RobotCommandWrapperDto::class.java
        )
        val recordedBlockingCommandDTOs: List<BlockCommandDto> = recordedRobotCommandWrapperDTO.commands.map {
                x -> BlockCommandDto.makeFromSerializedString(x)
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
*/
    /*
    @Test
    fun shouldAllowToSendMovementItemUseCommands() {
        //given
        val inputCommands = listOf(
            UseItemMovementCommandDto(UUID.randomUUID(), "ANY_NAME", UUID.randomUUID()),
            UseItemMovementCommandDto(UUID.randomUUID(), "ANY_NAME", UUID.randomUUID())
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
            RobotCommandWrapperDto::class.java
        )
        val recordedMovementItemUseCommandDTOs: List<UseItemMovementCommandDto> = recordedRobotCommandWrapperDTO.commands.map {
                x -> UseItemMovementCommandDto.makeFromSerializedString(x)
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
*/
    @Test
    fun shouldAllowToSendMovementCommands() {
        //given
        val inputCommands = listOf(
            MovementCommandDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()),
            MovementCommandDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
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
            RobotCommandWrapperDto::class.java
        )
        val recordedMovementCommandDtos: List<MovementCommandDto> = recordedRobotCommandWrapperDTO.commands.map {
                x -> MovementCommandDto.fromString(x)
        }

        //then
        assertThat(recordedRequest.method)
            .isEqualTo("POST")
        assertThat(recordedRequest.path)
            .isEqualTo("/commands")
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        //and
        assertThat(recordedMovementCommandDtos)
            .isEqualTo(inputCommands)
    }
/*
    @Test
    fun shouldAllowToSendBattleItemUseCommands() {
        //given
        val inputCommands = listOf(
            UseItemFightCommandDto(UUID.randomUUID(), "ANY_NAME", UUID.randomUUID(), UUID.randomUUID()),
            UseItemFightCommandDto(UUID.randomUUID(), "ANY_NAME", UUID.randomUUID(), UUID.randomUUID())
        )
        val mockResponse = MockResponse()
            .setResponseCode(202)
        mockWebServer!!.enqueue(mockResponse)

        //when
        robotCommandDispatcherClient!!.sendBattleItemUseCommands(inputCommands)

        //and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRobotCommandWrapperDTO = objectMapper.readValue(
            recordedRequest.body.readUtf8(),
            RobotCommandWrapperDto::class.java
        )
        val recordedBattleItemUseCommandDTOs: List<UseItemFightCommandDto> = recordedRobotCommandWrapperDTO.commands.map {
                x -> UseItemFightCommandDto.makeFromSerializedString(x)
        }

        //then
        assertThat(recordedRequest.method)
            .isEqualTo("POST")
        assertThat(recordedRequest.path)
            .isEqualTo("/commands")
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        //and
        assertThat(recordedBattleItemUseCommandDTOs)
            .isEqualTo(inputCommands)
    }
*/
    @Test
    fun shouldAllowToSendBattleCommands() {
        //given
        val inputCommands = listOf(
            FightCommandDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()),
            FightCommandDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
        )
        val mockResponse = MockResponse()
            .setResponseCode(202)
        mockWebServer!!.enqueue(mockResponse)

        //when
        robotCommandDispatcherClient!!.sendBattleCommands(inputCommands)

        //and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRobotCommandWrapperDTO = objectMapper.readValue(
            recordedRequest.body.readUtf8(),
            RobotCommandWrapperDto::class.java
        )
        val recordedBattleCommandDTOs: List<FightCommandDto> = recordedRobotCommandWrapperDTO.commands.map {
                x -> FightCommandDto.makeFromSerializedString(x)
        }

        //then
        assertThat(recordedRequest.method)
            .isEqualTo("POST")
        assertThat(recordedRequest.path)
            .isEqualTo("/commands")
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        //and
        assertThat(recordedBattleCommandDTOs)
            .isEqualTo(inputCommands)
    }

    @Test
    fun shouldAllowToSendMiningCommands() {
        //given
        val inputCommands = listOf(
            MineCommandDto(UUID.randomUUID(), UUID.randomUUID()),
            MineCommandDto(UUID.randomUUID(), UUID.randomUUID())
        )
        val mockResponse = MockResponse()
            .setResponseCode(202)
        mockWebServer!!.enqueue(mockResponse)

        //when
        robotCommandDispatcherClient!!.sendMiningCommands(inputCommands)

        //and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRobotCommandWrapperDTO = objectMapper.readValue(
            recordedRequest.body.readUtf8(),
            RobotCommandWrapperDto::class.java
        )
        val recordedMiningCommandDTOs: List<MineCommandDto> = recordedRobotCommandWrapperDTO.commands.map {
                x -> MineCommandDto.fromString(x)
        }

        //then
        assertThat(recordedRequest.method)
            .isEqualTo("POST")
        assertThat(recordedRequest.path)
            .isEqualTo("/commands")
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        //and
        assertThat(recordedMiningCommandDTOs)
            .isEqualTo(inputCommands)
    }
/*
    @Test
    fun shouldAllowToSendRepairItemUseCommands() {
        //given
        val inputCommands = listOf(
            UseItemRepairCommandDto(UUID.randomUUID(), "ANY_NAME", UUID.randomUUID()),
            UseItemRepairCommandDto(UUID.randomUUID(), "ANY_NAME", UUID.randomUUID())
        )
        val mockResponse = MockResponse()
            .setResponseCode(202)
        mockWebServer!!.enqueue(mockResponse)

        //when
        robotCommandDispatcherClient!!.sendRepairItemUseCommands(inputCommands)

        //and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRobotCommandWrapperDto = objectMapper.readValue(
            recordedRequest.body.readUtf8(),
            RobotCommandWrapperDto::class.java
        )
        val recordedRepairItemUseCommandDTOs: List<UseItemRepairCommandDto> = recordedRobotCommandWrapperDto.commands.map {
                x -> UseItemRepairCommandDto.makeFromSerializedString(x)
        }

        //then
        assertThat(recordedRequest.method)
            .isEqualTo("POST")
        assertThat(recordedRequest.path)
            .isEqualTo("/commands")
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        //and
        assertThat(recordedRepairItemUseCommandDTOs)
            .isEqualTo(inputCommands)
    }
*/
    @Test
    fun shouldAllowToSendRegeneratingCommands() {
        //given
        val inputCommands = listOf(
            RegenerateCommandDto(UUID.randomUUID(), UUID.randomUUID()),
            RegenerateCommandDto(UUID.randomUUID(), UUID.randomUUID())
        )
        val mockResponse = MockResponse()
            .setResponseCode(202)
        mockWebServer!!.enqueue(mockResponse)

        //when
        robotCommandDispatcherClient!!.sendRegeneratingCommands(inputCommands)

        //and
        val recordedRequest = mockWebServer!!.takeRequest()
        val recordedRobotCommandWrapperDto = objectMapper.readValue(
            recordedRequest.body.readUtf8(),
            RobotCommandWrapperDto::class.java
        )
        val recordedRegeneratingCommandDTOs: List<RegenerateCommandDto> = recordedRobotCommandWrapperDto.commands.map {
                x -> RegenerateCommandDto.makeFromSerializedString(x)
        }

        //then
        assertThat(recordedRequest.method)
            .isEqualTo("POST")
        assertThat(recordedRequest.path)
            .isEqualTo("/commands")
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE))
            .isEqualTo(MediaType.APPLICATION_JSON.toString())

        //and
        assertThat(recordedRegeneratingCommandDTOs)
            .isEqualTo(inputCommands)
    }
}