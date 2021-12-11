package microservice.dungeon.game.contracts

interface RestProducerContract {

    fun getRequestVerb(): String

    fun getRequestPath(): String

    fun getRequestHttpHeaderContentType(): String

    fun getExpectedResponseCode(): Int

    fun getExpectedResponseBody(): String
}