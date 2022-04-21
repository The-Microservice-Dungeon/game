package microservice.dungeon.game.unittests.model.game.services

import microservice.dungeon.game.aggregates.game.domain.TimeFrame
import microservice.dungeon.game.aggregates.game.services.GameLoopClock
import org.junit.Test
import org.mockito.InOrder
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock

internal class GameLoopClockTest {
  @Test
  fun shouldCallStateListenersInSemanticallyOrder() {
    val roundStartedListener = mock<() -> Unit>()
    val commandInputEndedListener = mock<() -> Unit>()
    val roundEndedListener = mock<() -> Unit>()
    val clock = GameLoopClock(TimeFrame(1,10))
    clock.registerOnRoundStart(roundStartedListener)
    clock.registerOnRoundEnd(roundEndedListener)
    clock.registerOnCommandInputEnded(commandInputEndedListener)
    clock.registerOnRoundEnd { clock.stop() }

    clock.run()

    val inOrder = inOrder(roundStartedListener, commandInputEndedListener, roundEndedListener)
    inOrder.verify(roundStartedListener).invoke()
    inOrder.verify(commandInputEndedListener).invoke()
    inOrder.verify(roundEndedListener).invoke()
  }
}
