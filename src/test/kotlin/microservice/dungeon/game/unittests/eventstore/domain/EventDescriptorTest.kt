package microservice.dungeon.game.unittests.eventstore.domain

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptorStatus
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import org.assertj.core.api.Assertions.assertThat
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.*

class EventDescriptorTest {
    private var environmentMock: Environment? = null
    private var applicationContextMock: ApplicationContext? = null

    private var someValidEvent: Event? = null

//    @BeforeEach
//    fun setUp() {
//        environmentMock = mock(Environment::class.java)
//        applicationContextMock = mock(ApplicationContext::class.java)
//
//        someValidEvent = RoundStarted(UUID.randomUUID(), EventTime.makeFromLocalDateTime(LocalDateTime.now()), UUID.randomUUID(), UUID.randomUUID(), 3, RoundStatus.COMMAND_INPUT_STARTED)
//    }
//
//
//    @Test
//    fun newEventDescriptorFromEvent() {
//        val eventDescriptor = EventDescriptor(someValidEvent!!)
//
//        assertThat(eventDescriptor)
//            .matches(someValidEvent!!)
//        assertThat(eventDescriptor.getStatus())
//            .isEqualTo(EventDescriptorStatus.CREATED)
//    }
//
//    @Test
//    fun getAsEventShouldBuildEventFromDescriptor() {
//        val validEventDescriptor = EventDescriptor(someValidEvent!!)
//        whenever(environmentMock!!.getProperty("eventStore.builderSuffix")).thenReturn("Builder")
//        whenever(applicationContextMock!!.getBean("roundStartedBuilder")).thenReturn(RoundStartedBuilder())
//
//        val createdEvent = validEventDescriptor.getAsEvent(environmentMock!!, applicationContextMock!!)
//
//        assertThat(createdEvent)
//            .isSameAs(someValidEvent!!)
//            .matches(validEventDescriptor)
//    }
//
//    @Test
//    fun getAsEventShouldThrowWhenEventBuilderDoesNotExist() {
//        val validEventDescriptor = EventDescriptor(someValidEvent!!)
//        whenever(environmentMock!!.getProperty("eventStore.builderSuffix")).thenReturn("Builder")
//        whenever(applicationContextMock!!.getBean("roundStartedBuilder")).thenThrow(RuntimeException::class.java)
//
//        assertThatThrownBy {
//            validEventDescriptor.getAsEvent(environmentMock!!, applicationContextMock!!)
//        }
//    }
}