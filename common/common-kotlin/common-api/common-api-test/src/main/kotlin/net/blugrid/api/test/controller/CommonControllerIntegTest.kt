package net.blugrid.api.test.controller

import net.blugrid.api.test.generator.*
import io.micronaut.core.type.Argument
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.netty.handler.codec.http.HttpResponseStatus
import net.blugrid.api.common.model.resource.BaseCreateResource
import net.blugrid.api.common.model.resource.BaseResource
import net.blugrid.api.common.model.resource.BaseUpdateResource
import net.blugrid.api.common.organisation.pageOf
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert

open class CommonControllerIntegTest(
    open val baseUri: String,
    @Client("/") open val client: HttpClient
) {


    fun <T : BaseResource<out Any>> assertCreate(createPayload: BaseCreateResource<out Any>, responseType: Class<T>, uri: String = baseUri): T {
        return create(createPayload = createPayload, responseType = responseType, uri = uri)
    }

    fun <T : BaseResource<out Any>> create(createPayload: BaseCreateResource<out Any>, responseType: Class<T>, uri: String = baseUri): T {
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

    fun <T : BaseResource<out Any>> assertUpdate(updatePayload: BaseUpdateResource<out Any>, responseType: Class<T>, uri: String = baseUri): T {
        return update(updatePayload = updatePayload, responseType = responseType, uri = uri)
    }

    fun <T : BaseResource<out Any>> update(updatePayload: BaseUpdateResource<out Any>, responseType: Class<T>, uri: String = baseUri): T {
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

    fun <T : BaseResource<out Any>> assertGetById(
        createPayload: BaseCreateResource<out Any>,
        responseType: Class<T>,
        uri: String = baseUri
    ): T {
        val newResource = create(createPayload, responseType)
        return getById(id = newResource.id.value, responseType = responseType, uri = uri)
    }

    fun <T : BaseResource<out Any>> getById(id: Long, responseType: Class<T>, uri: String = baseUri): T {
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

    fun <T : BaseResource<out Any>> assertGetPage(
        resources: List<BaseCreateResource<out Any>>,
        pageable: Pageable,
        responseType: Class<T>,
        uri: String = baseUri
    ): Page<T> {
        resources.forEach {
            assertCreate(it, responseType)
        }
        return getPage(pageable = pageable, responseType = responseType, uri = uri)
    }

    fun <T : BaseResource<out Any>> getPage(pageable: Pageable, responseType: Class<T>, uri: String = baseUri): Page<T> {
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

    fun <T : BaseResource<out Any>> assertDelete(createPayload: BaseCreateResource<out Any>, responseType: Class<T>, uri: String = baseUri) {
        val newResource = assertCreate(createPayload, responseType)
        delete(id = newResource.id.value, uri = uri)
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
