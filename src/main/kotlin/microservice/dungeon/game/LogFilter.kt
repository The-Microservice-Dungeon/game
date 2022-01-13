package microservice.dungeon.game

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply

class LogFilter: Filter<ILoggingEvent>() {

    override fun decide(event: ILoggingEvent): FilterReply {
        return if (event.loggerName.startsWith("microservice.dungeon.game")) {
            FilterReply.ACCEPT
        } else {
            FilterReply.DENY
        }
    }
}