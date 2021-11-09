package microservice.dungeon.game.model.round

import microservice.dungeon.game.aggregates.core.EntityAlreadyExistsException
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.web.CommandDispatcherClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.*

class RoundServiceTests {
    private var roundRepositoryMock: RoundRepository? = null
    private var eventStoreServiceMock: EventStoreService? = null
    private var eventPublisherServiceMock: EventPublisherService? = null
    private var commandDispatcherClientMock: CommandDispatcherClient? = null
    private var roundService: RoundService? = null

    @BeforeEach
    fun setup() {
        roundRepositoryMock = mock(RoundRepository::class.java)
        eventStoreServiceMock = mock(EventStoreService::class.java)
        eventPublisherServiceMock = mock(EventPublisherService::class.java)
        commandDispatcherClientMock = mock(CommandDispatcherClient::class.java)
        roundService = RoundService(roundRepositoryMock!!, eventStoreServiceMock!!, eventPublisherServiceMock!!, commandDispatcherClientMock!!)
    }

    @Test
    fun startNewRoundSuccessfulTest() {
        val gameId:UUID = UUID.randomUUID()
        val roundNumber: Int = 3

        whenever(roundRepositoryMock!!.findByGameIdAndRoundNumber(gameId, roundNumber)).thenReturn(Optional.empty())

        val roundId: UUID = roundService!!.startNewRound(gameId, roundNumber)

        val roundArg = argumentCaptor<Round>()
        verify(roundRepositoryMock!!).save(roundArg.capture())
        assertEquals(roundArg.firstValue.getRoundId(), roundId)
        assertEquals(roundArg.firstValue.getRoundNumber(), roundNumber)
        assertEquals(roundArg.firstValue.getGameId(), gameId)

        val roundStartedArg = argumentCaptor<RoundStarted>()
        verify(eventStoreServiceMock!!).storeEvent(roundStartedArg.capture())
        assertEquals(roundStartedArg.firstValue.getRoundId(), roundId)
        assertEquals(roundStartedArg.firstValue.getGameId(), gameId)
        assertEquals(roundStartedArg.firstValue.getRoundNumber(), roundNumber)
        assertTrue(roundStartedArg.firstValue.getOccurredAt() <= LocalDateTime.now())
        assertEquals(roundStartedArg.firstValue.getRoundStatus(), RoundStatus.COMMAND_INPUT_STARTED)

        verify(eventPublisherServiceMock!!).publishEvents(listOf(roundStartedArg.firstValue))
    }

    @Test
    fun startNewRoundWhenRoundAlreadyExistsTest() {
        val gameId:UUID = UUID.randomUUID()
        val roundNumber: Int = 3

        whenever(roundRepositoryMock!!.findByGameIdAndRoundNumber(gameId, roundNumber)).thenReturn(Optional.of(Round(gameId, roundNumber)))

        assertThrows(EntityAlreadyExistsException::class.java) {
            val roundId: UUID = roundService!!.startNewRound(gameId, roundNumber)
        }
    }

    @Test
    fun endCommandInputsTest() {
        val roundId = UUID.randomUUID()
        val gameId = UUID.randomUUID()
        val roundNumber = 5
        whenever(roundRepositoryMock!!.findById(any(UUID::class.java))).thenReturn(Optional.of(Round(gameId, roundNumber, roundId)))

        roundService!!.endCommandInputs(roundId)

        verify(roundRepositoryMock!!).findById(roundId)
        verify(roundRepositoryMock!!).save(argThat { round ->
            round.getRoundStatus() == RoundStatus.COMMAND_INPUT_ENDED
        })
        val commandInputEndedArg = argumentCaptor<CommandInputEnded>()
        verify(eventStoreServiceMock!!).storeEvent(commandInputEndedArg.capture())
        assertEquals(commandInputEndedArg.firstValue.getRoundId(), roundId)
        assertEquals(commandInputEndedArg.firstValue.getGameId(), gameId)
        assertEquals(commandInputEndedArg.firstValue.getRoundNumber(), roundNumber)
        assertTrue(commandInputEndedArg.firstValue.getOccurredAt() <= LocalDateTime.now())
        assertEquals(commandInputEndedArg.firstValue.getRoundStatus(), RoundStatus.COMMAND_INPUT_ENDED)
        verify(eventPublisherServiceMock!!).publishEvents(listOf(commandInputEndedArg.firstValue))
    }

    //TODO("Command-Dispatching")

    @Test
    fun endRoundTest() {
        val roundId = UUID.randomUUID()
        val gameId = UUID.randomUUID()
        val roundNumber = 5
        whenever(roundRepositoryMock!!.findById(any(UUID::class.java))).thenReturn(Optional.of(Round(gameId, roundNumber, roundId)))

        roundService!!.endRound(roundId)

        verify(roundRepositoryMock!!).findById(roundId)
        verify(roundRepositoryMock!!).save(argThat { round ->
            round.getRoundStatus() == RoundStatus.ROUND_ENDED
        })
        val commandInputEndedArg = argumentCaptor<RoundEnded>()
        verify(eventStoreServiceMock!!).storeEvent(commandInputEndedArg.capture())
        assertEquals(commandInputEndedArg.firstValue.getRoundId(), roundId)
        assertEquals(commandInputEndedArg.firstValue.getGameId(), gameId)
        assertEquals(commandInputEndedArg.firstValue.getRoundNumber(), roundNumber)
        assertTrue(commandInputEndedArg.firstValue.getOccurredAt() <= LocalDateTime.now())
        assertEquals(commandInputEndedArg.firstValue.getRoundStatus(), RoundStatus.ROUND_ENDED)
        verify(eventPublisherServiceMock!!).publishEvents(listOf(commandInputEndedArg.firstValue))
    }
}