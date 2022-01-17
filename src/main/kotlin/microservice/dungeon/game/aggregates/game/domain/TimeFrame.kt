package microservice.dungeon.game.aggregates.game.domain

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class TimeFrame (
   private val totalRoundDurationInMS: Long,
   private val commandInputLengthInPercent: Int,
   private val timeStarted: LocalDateTime
) {
    constructor(totalRoundDurationInMS: Long, commandInputLengthInPercent: Int):
            this(totalRoundDurationInMS, commandInputLengthInPercent, LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))

    constructor(game: Game):
            this(game.getTotalRoundTimespanInMS(), game.getRelativeCommandInputTimespanInPercent(), LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))

    fun getCommandInputTimeFrameInMS(): Long {
        return (totalRoundDurationInMS * commandInputLengthInPercent) / 100
    }

    fun getExecutionTimeFrameInMS(): Long {
        val timePassedInMS = ChronoUnit.MILLIS.between(timeStarted, LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
        val remainingTimeInMS = totalRoundDurationInMS - timePassedInMS
        return if (remainingTimeInMS > 0) remainingTimeInMS else 0
    }
}