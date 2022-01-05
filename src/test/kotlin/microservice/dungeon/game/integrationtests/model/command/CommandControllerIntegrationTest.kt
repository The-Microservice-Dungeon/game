package microservice.dungeon.game.integrationtests.model.command

import microservice.dungeon.game.aggregates.command.controller.CommandController
import microservice.dungeon.game.aggregates.command.domain.Command
import microservice.dungeon.game.aggregates.command.domain.CommandObject
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.dtos.CommandDTO
import microservice.dungeon.game.aggregates.command.services.CommandService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.*

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "kafka.bootstrapAddress=localhost:29100"
    ]
)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29100", "port=29100"])
class CommandControllerIntegrationTest {
    private var mockCommandService: CommandService? = null
    private var commandController: CommandController? = null
    private var webTestClient: WebTestClient? = null


    @BeforeEach
    fun setUp() {
        mockCommandService = mock()
        commandController = CommandController(mockCommandService!!)
        webTestClient = WebTestClient.bindToController(commandController!!).build()
    }



    @Test
    fun contextLoads() {
    }

    @Test
    fun shouldReturnCommands() {
        val gameId = UUID.randomUUID()
        val roundNumber = 1

        val command = Command(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            CommandType.BATTLE,
            CommandObject(
                CommandType.BATTLE,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "",
                1
            ),
            roundNumber
        )

        whenever(mockCommandService!!.getAllRoundCommands(gameId, roundNumber))
            .thenReturn(listOf(command))

        val result = webTestClient!!.get().uri("/commands")

    }

    @Test
    fun shouldAllowToCreateNewCommand() {
        // given
        val requestEntity = makeAnyValidCommandDTO()
        val responseCommandId = UUID.randomUUID()
        whenever(mockCommandService!!.save(requestEntity))
            .thenReturn(responseCommandId)

        // when
        val result = webTestClient!!.post()
            .uri("/commands")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestEntity)
            .exchange()
            .expectStatus().isCreated
            .expectBody<UUID>()
            .returnResult()

        // then
        assertThat(result.responseBody!!)
            .isEqualTo(responseCommandId)

        // and
        verify(mockCommandService!!).save(requestEntity)
    }


    private fun makeAnyValidCommandDTO(): CommandDTO =
        CommandDTO(
            gameId = UUID.randomUUID(),
            playerId = UUID.randomUUID(),
            robotId = UUID.randomUUID(),
            commandType = CommandType.BATTLE,
            commandObject = CommandObject(
                commandType = CommandType.BATTLE,
                planetId = UUID.randomUUID(),
                targetId = UUID.randomUUID(),
                itemName = "ANY NAME",
                itemQuantity = 1
            )
        )
}