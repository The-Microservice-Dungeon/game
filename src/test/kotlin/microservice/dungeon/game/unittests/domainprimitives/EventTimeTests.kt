package microservice.dungeon.game.unittests.domainprimitives

import com.fasterxml.jackson.databind.ObjectMapper
import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.round.events.RoundEnded
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class EventTimeTests {
    private var objectMapper: ObjectMapper? = null

    private val timeFormatPattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private val validTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)


    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper().findAndRegisterModules()
    }

    @Test
    fun newEventTimeFromLocalDateTime() {
        val eventTime = EventTime.makeFromLocalDateTime(validTime)

        assertThat(eventTime.getTime())
            .isEqualTo(validTime.truncatedTo(ChronoUnit.SECONDS))
    }

    @Test
    fun newEventTimeFromFormattedTimestamp() {
        val validTimestamp = validTime.format(
            DateTimeFormatter.ofPattern(timeFormatPattern)
        )
        val event = EventTime.makeFromFormattedTimeStamp(validTimestamp)

        assertThat(event.getTime())
            .isEqualTo(validTime)
        assertThat(event.getFormattedTime())
            .isEqualTo(validTimestamp)
    }


    @Test
    fun getFormattedTimeShouldConformToDateFormatDecision() {
        val event = EventTime.makeFromLocalDateTime(validTime)

        assertThat(event.getFormattedTime())
            .isEqualTo(validTime.format(
                DateTimeFormatter.ofPattern(timeFormatPattern)
            ))
    }

    @Test
    fun eventTimeShouldBeSerializable() {
        val validEventTime = EventTime.makeFromLocalDateTime(validTime)

        val serializedEvent = objectMapper!!.writeValueAsString(validEventTime)
        val deserializedEvent = objectMapper!!.readValue(serializedEvent, EventTime::class.java)

        assertThat(validEventTime.getTime())
            .isEqualTo(deserializedEvent.getTime())
    }
}