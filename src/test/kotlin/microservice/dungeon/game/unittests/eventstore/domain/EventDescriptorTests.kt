package microservice.dungeon.game.unittests.eventstore.domain

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptorStatus
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.aggregates.round.events.RoundStartedBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.*

class EventDescriptorTests {

    @Test
    fun makeEventDescriptorFromEventTest() {
        val event: Event = RoundStarted(UUID.randomUUID(), LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
        val eventDescriptor = EventDescriptor(event)
        assertEquals(eventDescriptor.getId(), event.getId())
        assertEquals(eventDescriptor.getType(), event.getEventName())
        assertEquals(eventDescriptor.getOccurredAt(), event.getOccurredAt())
        assertEquals(eventDescriptor.getContent(), event.serialized())
        assertEquals(eventDescriptor.getStatus(), EventDescriptorStatus.CREATED)

    }

    @Test
    fun makeEventFromDescriptorTest() {
        val event: RoundStarted = RoundStarted(UUID.randomUUID(), LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
        val eventDescriptor = EventDescriptor(event)
        val environmentMock = mock(Environment::class.java)
        val applicationContextMock = mock(ApplicationContext::class.java)
        whenever(environmentMock.getProperty("eventStore.builderSuffix")).thenReturn("Builder")
        whenever(applicationContextMock.getBean("roundStartedBuilder")).thenReturn(RoundStartedBuilder())
        val deserializedEvent = eventDescriptor.getAsEvent(environmentMock, applicationContextMock) as RoundStarted

        assertTrue(deserializedEvent.equals(event))
    }

    @Test
    fun makeEventFromDescriptorNoBuilderFoundTest() {
        val event: RoundStarted = RoundStarted(UUID.randomUUID(), LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
        val eventDescriptor = EventDescriptor(event)
        val environmentMock = mock(Environment::class.java)
        val applicationContextMock = mock(ApplicationContext::class.java)
        whenever(environmentMock.getProperty("eventStore.builderSuffix")).thenReturn("Builder")
        whenever(applicationContextMock.getBean("roundStartedBuilder")).thenThrow(RuntimeException::class.java)

        assertThrows(Exception::class.java) {
            val deserializedEvent = eventDescriptor.getAsEvent(environmentMock, applicationContextMock) as RoundStarted
        }
    }
}