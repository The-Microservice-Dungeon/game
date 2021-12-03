package microservice.dungeon.game.contracttests.model.player.events

import microservice.dungeon.game.aggregates.domainprimitives.EventTime
import microservice.dungeon.game.aggregates.player.dtos.PlayerEventDto
import microservice.dungeon.game.aggregates.player.events.PlayerCreated
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
//import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.time.LocalDateTime
import java.util.*

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "kafka.bootstrapAddress=localhost:29103"
    ])
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:29103", "port=29103"])
class PlayerCreatedContractTest {
    private val anyEventId = UUID.randomUUID()
    private val anyPlayerId = UUID.randomUUID()
    private val anyEventTime = EventTime.makeFromLocalDateTime(LocalDateTime.now())
    private val anyUsername = "ANY_USERNAME"
    private val anyMailAddress = "ANY_MAILADDRESS"

    private val playerCreatedEvent = PlayerCreated(anyEventId, anyEventTime, anyPlayerId, anyUsername, anyMailAddress)


    @Test
    fun shouldSerializeAndConformToSpecification() {
        // given
        val playerEventDto = playerCreatedEvent.toDTO() as PlayerEventDto

        // when then
        assertThat(playerEventDto)
            .containsPlayerId(anyPlayerId)
        assertThat(playerEventDto)
            .containsUserName(anyUsername)
        assertThat(playerEventDto)
            .containsMailAddress(anyMailAddress)
    }
}