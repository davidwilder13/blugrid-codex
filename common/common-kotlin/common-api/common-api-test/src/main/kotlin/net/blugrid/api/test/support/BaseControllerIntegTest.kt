package net.blugrid.api.test.support

import io.github.serpro69.kfaker.faker
import io.micronaut.core.type.Argument
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.netty.handler.codec.http.HttpResponseStatus
import jakarta.inject.Inject
import net.blugrid.api.common.model.resource.GenericCreateResource
import net.blugrid.api.common.model.resource.GenericResource
import net.blugrid.api.common.model.resource.GenericUpdateResource
import net.blugrid.api.common.organisation.pageOf
import net.blugrid.api.logging.logger
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@MicronautTest(environments = ["debug-logging", "json", "security", "db"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseControllerIntegTest(
    open val baseUri: String,
) : PostgresTestSupport {

    val log = logger()

    @Inject
    @field:Client("/")
    protected lateinit var client: HttpClient

    @Inject
    lateinit var embeddedServer: EmbeddedServer

    @BeforeAll
    fun logPort() {
        log.info("Embedded server started on port: ${embeddedServer.port}")
    }

    protected val faker = faker {}

    fun <T : GenericResource<out Any>> assertCreate(createPayload: GenericCreateResource<out Any>, responseType: Class<T>, uri: String = baseUri): T {
        return create(createPayload = createPayload, responseType = responseType, uri = uri)
    }

    fun <T : GenericResource<out Any>> create(
        createPayload: GenericCreateResource<out Any>,
        responseType: Class<T>,
        uri: String = baseUri
    ): T {
        return with(
            client.toBlocking()
                .exchange(
                    HttpRequest.PUT(uri, createPayload),
                    responseType,
                ),
        ) {
            MatcherAssert.assertThat(status.code, CoreMatchers.equalTo(HttpResponseStatus.OK.code()))
            body.get()
        }
    }

    fun <T : GenericResource<out Any>> assertUpdate(updatePayload: GenericUpdateResource<out Any>, responseType: Class<T>, uri: String = baseUri): T {
        return update(updatePayload = updatePayload, responseType = responseType, uri = uri)
    }

    fun <T : GenericResource<out Any>> update(updatePayload: GenericUpdateResource<out Any>, responseType: Class<T>, uri: String = baseUri): T {
        return with(
            receiver = client.toBlocking()
                .exchange(
                    HttpRequest.POST("$uri/${updatePayload.id}", updatePayload),
                    Argument.of(responseType),
                ),
        ) {
            MatcherAssert.assertThat(status.code, CoreMatchers.equalTo(HttpResponseStatus.OK.code()))
            body.get()
        }
    }

    fun <T : GenericResource<out Any>> assertGetById(
        createPayload: GenericCreateResource<out Any>,
        responseType: Class<T>,
        uri: String = baseUri
    ): T {
        val newResource = create(createPayload, responseType)
        return getById(id = newResource.id, responseType = responseType, uri = uri)
    }

    fun <T : GenericResource<out Any>> getById(id: Long, responseType: Class<T>, uri: String = baseUri): T {
        return with(
            receiver = client.toBlocking()
                .exchange(
                    HttpRequest.GET<Unit>("$uri/${id}"),
                    Argument.of(responseType),
                ),
        ) {
            MatcherAssert.assertThat(status.code, CoreMatchers.equalTo(HttpResponseStatus.OK.code()))
            body.get()
        }
    }

    fun <T : GenericResource<out Any>> assertGetPage(
        resources: List<GenericCreateResource<out Any>>,
        pageable: Pageable,
        responseType: Class<T>,
        uri: String = baseUri
    ): Page<T> {
        resources.forEach {
            assertCreate(it, responseType)
        }
        return getPage(pageable = pageable, responseType = responseType, uri = uri)
    }

    fun <T : GenericResource<out Any>> getPage(pageable: Pageable, responseType: Class<T>, uri: String = baseUri): Page<T> {
        return with(
            receiver = client.toBlocking()
                .exchange(
                    HttpRequest.POST(uri, pageable),
                    pageOf(responseType),
                ),
        ) {
            MatcherAssert.assertThat(status.code, CoreMatchers.equalTo(HttpResponseStatus.OK.code()))
            body.get()
        }
    }

    fun <T : GenericResource<out Any>> assertDelete(createPayload: GenericCreateResource<out Any>, responseType: Class<T>, uri: String = baseUri) {
        val newResource = assertCreate(createPayload, responseType)
        delete(id = newResource.id, uri = uri)
    }

    fun delete(id: Long, uri: String = baseUri) {
        with(
            receiver = client.toBlocking()
                .exchange(
                    HttpRequest.DELETE<Void>("$uri/$id"),
                    Argument.of(Void::class.java),
                ),
        ) {
            MatcherAssert.assertThat(status.code, CoreMatchers.equalTo(HttpResponseStatus.OK.code()))
        }
    }
}
