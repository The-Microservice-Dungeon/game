package microservice.dungeon.game.integrationtests.model.command.controller

import microservice.dungeon.game.aggregates.command.controller.CommandController
import microservice.dungeon.game.aggregates.command.controller.dto.CommandObjectRequestDto
import microservice.dungeon.game.aggregates.command.controller.dto.CommandRequestDto
import microservice.dungeon.game.aggregates.command.controller.dto.CommandResponseDto
import microservice.dungeon.game.aggregates.command.domain.CommandArgumentException
import microservice.dungeon.game.aggregates.command.domain.CommandType
import microservice.dungeon.game.aggregates.command.repositories.CommandRepository
import microservice.dungeon.game.aggregates.command.services.CommandService
import microservice.dungeon.game.aggregates.game.controller.dto.CreateGameResponseDto
import microservice.dungeon.game.aggregates.game.domain.GameNotFoundException
import microservice.dungeon.game.aggregates.game.domain.GameStateException
import microservice.dungeon.game.aggregates.player.domain.PlayerNotFoundException
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.*

class CommandControllerIntegrationTest {
    private var mockCommandRepository: CommandRepository? = null
    private var mockCommandService: CommandService? = null
    private var commandController: CommandController? = null
    private var webTestClient: WebTestClient? = null


    @BeforeEach
    fun setUp() {
        mockCommandRepository = mock()
        mockCommandService = mock()
        commandController = CommandController(mockCommandService!!, mockCommandRepository!!)
        webTestClient = WebTestClient.bindToController(commandController!!).build()
    }

    @Test
    fun shouldAllowToCreateCommands() {
        // given
        val transactionId = UUID.randomUUID()
        val requestBody = CommandRequestDto(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "blocking", CommandObjectRequestDto(
                "blocking", null, null, null, null
            )
        )
        whenever(mockCommandService!!.createNewCommand(any(), any(), any(), any(), any()))
            .thenReturn(transactionId)

        // when
        val result = webTestClient!!.post().uri("/commands")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<CommandResponseDto>()
            .returnResult()
        val responseBody: CommandResponseDto = result.responseBody!!

        // then
        verify(mockCommandService!!).createNewCommand(
            requestBody.gameId, requestBody.playerToken, requestBody.robotId, CommandType.BLOCKING, requestBody
        )
    }

    @Test
    fun shouldRespondBadRequestWhenCommandTypeNotValidWhileTryingToCreateNewCommand() {
        // given
        val requestBody = CommandRequestDto(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "wrong commandType", CommandObjectRequestDto(
                "this one does not matter", null, null, null, null
            )
        )

        // when then
        webTestClient!!.post().uri("/commands")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun shouldRespondNotFoundWhenCatchingPlayerNotFoundExceptionWhileTryingToCreateNewCommand() {
        val requestBody = CommandRequestDto(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "blocking", CommandObjectRequestDto(
                "blocking", null, null, null, null
            )
        )
        doThrow(PlayerNotFoundException("Player not found.")).whenever(mockCommandService!!)
            .createNewCommand(any(), any(), any(), any(), any())

        // when then
        webTestClient!!.post().uri("/commands")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun shouldRespondNotFoundWhenCatchingGameNotFoundExceptionWhileTryingToCreateNewCommand() {
        val requestBody = CommandRequestDto(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "blocking", CommandObjectRequestDto(
                "blocking", null, null, null, null
            )
        )
        doThrow(GameNotFoundException("Game not found.")).whenever(mockCommandService!!)
            .createNewCommand(any(), any(), any(), any(), any())

        // when then
        webTestClient!!.post().uri("/commands")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun shouldRespondForbiddenWhenCatchingGameStateExceptionWhenTryingToCreateNewCommand() {
        val requestBody = CommandRequestDto(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "blocking", CommandObjectRequestDto(
                "blocking", null, null, null, null
            )
        )
        doThrow(GameStateException("Game has not started yet.")).whenever(mockCommandService!!)
            .createNewCommand(any(), any(), any(), any(), any())

        // when then
        webTestClient!!.post().uri("/commands")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun shouldRespondForbiddenWhenCatchingCommandArgumentExceptionWhileTryingToCreateNewCommand() {
        val requestBody = CommandRequestDto(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "blocking", CommandObjectRequestDto(
                "blocking", null, null, null, null
            )
        )
        doThrow(CommandArgumentException("Robot not found.")).whenever(mockCommandService!!)
            .createNewCommand(any(), any(), any(), any(), any())

        // when then
        webTestClient!!.post().uri("/commands")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isForbidden
    }

//    @Test
//    fun shouldReturnCommands() {
//        val gameId = UUID.randomUUID()
//        val roundNumber = 1
//
//        val command = Command(
//            UUID.randomUUID(),
//            gameId,
//            UUID.randomUUID(),
//            UUID.randomUUID(),
//            CommandType.BATTLE,
//            CommandPayload(
//                CommandType.BATTLE,
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                "",
//                1
//            ),
//            roundNumber
//        )
//
//        whenever(mockCommandService!!.getAllRoundCommands(command.gameId, roundNumber))
//            .thenReturn(listOf(command))
//
//        val uri = URI.create("/commands?gameId=${command.gameId}&roundNumber=${command.roundNumber}")
//
//        val result = webTestClient!!.get()
//            .uri(uri)
//            .exchange()
//            .expectStatus().isOk
//            .expectBody<List<Command>>()
//            .returnResult()
//
//        println(uri.toString())
//        println(listOf(command))
//        println(result.responseBody!!)
//        assertThat(result.responseBody!!.contains(command))
//
//    }
//
//    @Test
//    fun shouldAllowToCreateNewCommand() {
//        // given
//        val requestEntity = makeAnyValidCommandDTO()
//        val responseCommandId = UUID.randomUUID()
//        whenever(mockCommandService!!.save(requestEntity))
//            .thenReturn(responseCommandId)
//
//        // when
//        val result = webTestClient!!.post()
//            .uri("/commands")
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON)
//            .bodyValue(requestEntity)
//            .exchange()
//            .expectStatus().isCreated
//            .expectBody<CommandResponseDTO>()
//            .returnResult()
//
//        // then
//        println(result.responseBody)
//        assertThat(result.responseBody!!.commandId)
//            .isEqualTo(responseCommandId)
//
//        // and
//        verify(mockCommandService!!).save(requestEntity)
//    }
//
//
//    private fun makeAnyValidCommandDTO(): CommandDTO =
//        CommandDTO(
//            gameId = UUID.randomUUID(),
//            playerToken = UUID.randomUUID(),
//            robotId = UUID.randomUUID(),
//            commandType = CommandType.BATTLE,
//            commandPayload = CommandPayload(
//                commandType = CommandType.BATTLE,
//                planetId = UUID.randomUUID(),
//                targetId = UUID.randomUUID(),
//                itemName = "ANY NAME",
//                itemQuantity = 1
//            )
//        )
}