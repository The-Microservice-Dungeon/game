package microservice.dungeon.game.aggregates.domainprimitives

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class EventTime (
    private val time: LocalDateTime
) {
    fun getTime(): LocalDateTime = time

    @JsonIgnore
    fun getFormattedTime(): String = time.format(
        DateTimeFormatter.ofPattern(timeFormat)
    )

    @JsonIgnore
    fun getTimeFormat(): String = timeFormat


    companion object {
        const val timeFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        fun makeFromFormattedTimeStamp(timestamp: String): EventTime {
            return EventTime(
                LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(timeFormat))
            )
        }

        fun makeFromLocalDateTime(time: LocalDateTime): EventTime {
            return EventTime(time.truncatedTo(ChronoUnit.SECONDS))
        }
    }
}