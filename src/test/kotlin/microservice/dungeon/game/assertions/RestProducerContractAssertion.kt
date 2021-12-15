package microservice.dungeon.game.assertions

import microservice.dungeon.game.contracts.RestProducerContract
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpHeaders

class RestProducerContractAssertion(actual: RestProducerContract):
    AbstractObjectAssert<RestProducerContractAssertion, RestProducerContract>(actual, RestProducerContractAssertion::class.java) {

    fun conformsWithRequest(request: RecordedRequest): RestProducerContractAssertion {
        assertThat(actual.getRequestVerb())
            .isEqualTo(request.method)
        assertThat(actual.getRequestPath())
            .isEqualTo(request.path)
        assertThat(actual.getRequestHttpHeaderContentType())
            .isEqualTo(request.getHeader(HttpHeaders.CONTENT_TYPE))
        return this
    }

    fun conformsWithRequestBody(request: String): RestProducerContractAssertion {
        assertThat(actual.getExpectedResponseBody())
            .isEqualTo(request)
        return this
    }
}