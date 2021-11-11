package microservice.dungeon.game.unittests.eventconsumer

import microservice.dungeon.game.aggregates.eventconsumer.domain.EventMessage
import microservice.dungeon.game.aggregates.eventconsumer.repositories.EventMessageRepository
import microservice.dungeon.game.aggregates.eventconsumer.services.EventConsumerService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class EventConsumerServiceTests {
    private var eventMessageRepositoryMock: EventMessageRepository? = null
    private var eventConsumerService: EventConsumerService? = null

    @BeforeEach
    fun initialize() {
        eventMessageRepositoryMock = mock()
        eventConsumerService = EventConsumerService(eventMessageRepositoryMock!!)
    }

    @Test
    fun consumeMessageTest() {
        val messageId = UUID.randomUUID()
        val occurredAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        val callbackNum = CallbackCounter()
        val callback: () -> Unit = { callbackNum.counter = callbackNum.counter + 1 }
        whenever(eventMessageRepositoryMock!!.findById(messageId)).thenReturn(Optional.empty())
        eventConsumerService!!.consumeMessage(messageId, occurredAt, callback)

        verify(eventMessageRepositoryMock!!).save(argThat { eventMessage ->
            eventMessage.getId() == messageId &&
                    eventMessage.getOccurredAt() == occurredAt &&
                    eventMessage.getReceivedAt() <= LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        })
        assertEquals(callbackNum.counter, 1)
    }

    @Test
    fun consumeMessageAlreadyExistsTest() {
        val messageId = UUID.randomUUID()
        val occurredAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        val callbackNum = CallbackCounter()
        val callback: () -> Unit = { callbackNum.counter = callbackNum.counter + 1 }
        whenever(eventMessageRepositoryMock!!.findById(messageId)).thenReturn(Optional.of(EventMessage(messageId, occurredAt, LocalDateTime.now())))
        eventConsumerService!!.consumeMessage(messageId, occurredAt, callback)

        verify(eventMessageRepositoryMock!!, never()).save(any())
        assertEquals(callbackNum.counter, 0)
    }

    class CallbackCounter {
        var counter = 0
    }
}