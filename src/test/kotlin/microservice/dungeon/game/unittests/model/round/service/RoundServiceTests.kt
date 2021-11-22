package microservice.dungeon.game.unittests.model.round.service

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventpublisher.EventPublisherService
import microservice.dungeon.game.aggregates.eventstore.services.EventStoreService
import microservice.dungeon.game.aggregates.round.domain.Round
import microservice.dungeon.game.aggregates.round.domain.RoundStatus
import microservice.dungeon.game.aggregates.round.events.AbstractRoundEvent
import microservice.dungeon.game.aggregates.round.events.CommandInputEnded
import microservice.dungeon.game.aggregates.round.events.RoundStarted
import microservice.dungeon.game.aggregates.round.repositories.RoundRepository
import microservice.dungeon.game.aggregates.round.services.RoundService
import microservice.dungeon.game.assertions.CustomAssertions.Companion.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.*

class RoundServiceTests {
    private var mockEventStoreService: EventStoreService? = null
    private var mockEventPublisherService: EventPublisherService? = null
    private var mockRoundRepository: RoundRepository? = null
    private var roundService: RoundService? = null

    private val ANY_ROUND_ID = UUID.randomUUID()
    private val ANY_GAMEID = UUID.randomUUID()
    private val ANY_ROUND_NUMBER = 3


    @BeforeEach
    fun setUp() {
        mockEventStoreService = mock()
        mockEventPublisherService = mock()
        mockRoundRepository = mock()
        roundService = RoundService(mockRoundRepository!!, mockEventStoreService!!, mockEventPublisherService!!)
    }


    @Test
    fun shouldAllowNewRoundCreation() {
        // given
        // when
        val roundId = roundService!!.startNewRound(ANY_GAMEID, ANY_ROUND_NUMBER)

        // then
        argumentCaptor<Round>().apply {
            verify(mockRoundRepository!!).save(capture())

            val round = firstValue
            assertThat(round.getGameId())
                .isEqualTo(ANY_GAMEID)
            assertThat(round.getRoundNumber())
                .isEqualTo(ANY_ROUND_NUMBER)
            assertThat(round.getRoundStatus())
                .isEqualTo(RoundStatus.COMMAND_INPUT_STARTED)
        }
    }

    @Test
    fun shouldStoreRoundStartedWhenNewRoundCreated() {
        var round: Round? = null
        var roundStarted: Event? = null

        // given
        // when
        val roundId = roundService!!.startNewRound(ANY_GAMEID, ANY_ROUND_NUMBER)


        // then
        argumentCaptor<Round>().apply {
            verify(mockRoundRepository!!).save(capture())
            round = firstValue
        }
        argumentCaptor<Event>().apply {
            verify(mockEventStoreService!!).storeEvent(capture())
            roundStarted = firstValue
        }
        assertThat(roundStarted!!)
            .isInstanceOf(RoundStarted::class.java)
        assertThat(roundStarted!!.getTransactionId())
            .isEqualTo(roundId)
        assertThat(roundStarted!! as AbstractRoundEvent)
            .matches(round!!)
    }

    @Test
    fun shouldPublishRoundStartedWhenNewRoundCreated() {
        var round: Round? = null
        var roundStarted: Event? = null

        // given
        // when
        val roundId = roundService!!.startNewRound(ANY_GAMEID, ANY_ROUND_NUMBER)

        // then
        argumentCaptor<Round>().apply {
            verify(mockRoundRepository!!).save(capture())
            round = firstValue
        }
        argumentCaptor<List<Event>>().apply {
            verify(mockEventPublisherService!!).publishEvents(capture())
            roundStarted = firstValue.first()
        }
        assertThat(roundStarted!!)
            .isInstanceOf(RoundStarted::class.java)
        assertThat(roundStarted!!.getTransactionId())
            .isEqualTo(roundId)
        assertThat(roundStarted!! as AbstractRoundEvent)
            .matches(round!!)
    }

    @Test
    fun shouldSendPassiveScoutingCommandToRobotWhenNewRoundCreated() {
        //TODO
    }

    @Test
    fun shouldNotAllowNewRoundCreationWhenSameRoundAlreadyExists() {
        // given
        val duplicateGameId = ANY_GAMEID
        val duplicateRoundNumber = ANY_ROUND_NUMBER
        whenever(mockRoundRepository!!.findByGameIdAndRoundNumber(duplicateGameId, duplicateRoundNumber))
            .thenReturn(Optional.of(Round(duplicateGameId, duplicateRoundNumber)))

        // when then
        assertThatThrownBy {
            roundService!!.startNewRound(duplicateGameId, duplicateRoundNumber)
        }
    }



    @Test
    fun shouldAllowEndCommandInput() {
        // given
        val spyRound = spy(Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.COMMAND_INPUT_STARTED))
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(spyRound))

        // when
        roundService!!.endCommandInputs(ANY_GAMEID)

        // then
        verify(spyRound).endCommandInputPhase()
        verify(mockRoundRepository!!).save(isA<Round>())
    }

    @Test
    fun shouldStoreCommandInputEndedWhenCommandInputEnded() {
        var roundCommandInputEnded: Round? = null
        var commandInputEnded: Event? = null

        // given
        val roundCommandInputStarted = Round(ANY_GAMEID, ANY_ROUND_NUMBER, ANY_ROUND_ID, RoundStatus.COMMAND_INPUT_STARTED)
        whenever(mockRoundRepository!!.findById(ANY_GAMEID))
            .thenReturn(Optional.of(roundCommandInputStarted))

        // when
        roundService!!.endCommandInputs(ANY_GAMEID)

        // then
        argumentCaptor<Round>().apply {
            verify(mockRoundRepository!!).save(capture())
            roundCommandInputEnded = firstValue
        }
        argumentCaptor<Event>().apply {
            verify(mockEventStoreService!!).storeEvent(capture())
            commandInputEnded = firstValue
        }
        assertThat(commandInputEnded!!)
            .isInstanceOf(CommandInputEnded::class.java)
        assertThat(commandInputEnded!!.getTransactionId())
            .isEqualTo(roundCommandInputStarted.getRoundId())
        assertThat(commandInputEnded!! as AbstractRoundEvent)
            .matches(roundCommandInputEnded!!)
    }

    @Test
    fun shouldPublishCommandInputEndedWhenCommandInputEnded() {

    }













    @Test
    fun shouldNotAllowEndCommandInputWhenRoundNotExists() {

    }

    @Test
    fun shouldNotPublishEventWhenCommandInputAlreadyEnded() {

    }



    @Test
    fun shouldAllowDispatchBlockingCommands() {

    }

    @Test
    fun shouldSendBlockingCommandsToRobotWhenDispatchingBlockingCommands() {
        //TODO
    }

    @Test
    fun shouldAllowDispatchTradingCommands() {

    }

    @Test
    fun shouldSendTradingCommandsToRobotWhenDispatchingTradingCommands() {
        //TODO
    }

    @Test
    fun shouldAllowDispatchMovementCommands() {

    }

    @Test
    fun shouldSendMovementCommandsToRobotWhenDispatchingMovementCommands() {
        //TODO
    }

    @Test
    fun shouldAllowDispatchBattleCommands() {

    }

    @Test
    fun shouldSendBattleCommandsToRobotWhenDispatchingBattleCommands() {
        //TODO
    }

    @Test
    fun shouldAllowDispatchMiningCommands() {

    }

    @Test
    fun shouldSendMiningCommandsToRobotWhenDispatchingMiningCommands() {
        //TODO
    }



    @Test
    fun shouldAllowEndRound() {

    }

    @Test
    fun shouldPublishEventWhenRoundEnded() {

    }

    @Test
    fun shouldNotAllowEndRoundWhenRoundNotExists() {

    }

    @Test
    fun shouldNotPublishEventWhenRoundAlreadyEnded() {

    }
}