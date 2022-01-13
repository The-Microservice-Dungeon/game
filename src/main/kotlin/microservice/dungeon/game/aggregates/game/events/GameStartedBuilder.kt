package microservice.dungeon.game.aggregates.game.events

//@Component
//class GameStartedBuilder: EventBuilder {
//    override fun deserializedEvent(serialized: String): Event {
//        val objectMapper = ObjectMapper().findAndRegisterModules()
//        return objectMapper.readValue(serialized, GameStarted::class.java)
//    }
//}