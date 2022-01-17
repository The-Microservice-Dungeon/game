package microservice.dungeon.game.unittests.model.game.domain

import microservice.dungeon.game.aggregates.game.domain.TimeFrame
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TimeFrameTest {

    @Test
    fun shouldAllowToCalculateCommandInputTimeFrame() {
        // given
        val timeFrame = TimeFrame(60000, 75)

        // when
        val commandInputTimeFrameInMS: Long = timeFrame.getCommandInputTimeFrameInMS()

        // then
        assertThat(commandInputTimeFrameInMS)
            .isEqualTo(45000)
    }

    @Test
    fun shouldAllowToExecutionTimeFrame() {
        // given
        val timeFrame = TimeFrame(1000, 75)

        // when
        Thread.sleep(750)
        val executionTimeFrameInMs: Long = timeFrame.getExecutionTimeFrameInMS()

        println(executionTimeFrameInMs)

        // then
        assertThat(executionTimeFrameInMs)
            .isBetween(230, 250)
    }

    @Test
    fun shouldNotAllowExecutionTimeFrameToBecomeLowerThanZero() {
        // given
        val timeFrame = TimeFrame(100, 75)

        // when
        Thread.sleep(101)
        val executionTimeFrameInMS = timeFrame.getExecutionTimeFrameInMS()

        // then
        assertThat(executionTimeFrameInMS)
            .isEqualTo(0)
    }
}