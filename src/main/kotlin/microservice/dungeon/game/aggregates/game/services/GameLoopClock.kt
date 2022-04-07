package microservice.dungeon.game.aggregates.game.services

import microservice.dungeon.game.aggregates.game.domain.*
import microservice.dungeon.game.aggregates.game.events.GameStatusEvent
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.RoundStatusEvent
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class GameLoopClock(
    private var timeFrame: TimeFrame
) {
    private val logger = KotlinLogging.logger {}
    private val roundStartedListeners = mutableListOf<() -> Unit>()
    private val commandInputEndedListeners = mutableListOf<() -> Unit>()
    private val roundEndedListeners = mutableListOf<() -> Unit>()
    private val running = AtomicBoolean(false)

    fun run() {
        logger.debug { "Running game loop" }
        running.set(true)
        while (true) {
            if (!running.get()) {
                break
            }
            logger.info("Entered command acception phase. Waiting for {}s ...", (timeFrame.getCommandInputTimeFrameInMS().toDouble() / 1000))
            roundStartedListeners.forEach { it -> it() }
            Thread.sleep(timeFrame.getCommandInputTimeFrameInMS())

            if (!running.get()) {
                break;
            }
            logger.info("Entered command execution phase. Waiting for {}s ...", (timeFrame.getExecutionTimeFrameInMS().toDouble() / 1000))
            commandInputEndedListeners.forEach { it -> it() }
            Thread.sleep(timeFrame.getExecutionTimeFrameInMS())

            if (!running.get()) {
                break;
            }
            logger.info("Entered end round phase. Don't wait")
            roundEndedListeners.forEach { it -> it() }
        }
    }

    fun stop() {
        logger.debug { "Stopping game loop" }
        running.set(false)
    }

    fun patchTimeFrame(timeFrame: TimeFrame) {
        this.timeFrame = timeFrame
    }

    fun registerOnRoundStart(fn: () -> Unit) {
        roundStartedListeners.add(fn)
    }
    fun registerOnCommandInputEnded(fn: () -> Unit) {
        commandInputEndedListeners.add(fn)
    }
    fun registerOnRoundEnd(fn: () -> Unit) {
        roundEndedListeners.add(fn)
    }
}
