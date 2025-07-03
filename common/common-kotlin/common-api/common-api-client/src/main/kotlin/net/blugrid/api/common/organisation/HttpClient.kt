package net.blugrid.api.common.organisation

import io.micronaut.core.type.Argument
import io.micronaut.data.model.Page
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.cookie.Cookie
import io.netty.handler.codec.http.cookie.DefaultCookie
import net.blugrid.api.common.search.SearchPage

inline fun <T, reified U> HttpClient.post(
    path: String,
    payload: T,
    responseType: Argument<U>,
): HttpResponse<U> {
    return toBlocking().exchange(HttpRequest.POST(path, payload), responseType)
}

inline fun <T, reified U> HttpClient.put(
    path: String,
    payload: T,
    responseType: Argument<U>,
): HttpResponse<U> {
    return toBlocking().exchange(HttpRequest.PUT(path, payload), responseType)
}

inline fun <reified T> HttpClient.get(
    path: String,
    responseType: Argument<T>,
): HttpResponse<T> {
    return toBlocking().exchange(HttpRequest.GET<Unit>(path), responseType)
}

@Suppress("UNCHECKED_CAST")
fun <T> pageOf(type: Class<T>): Argument<Page<T>> {
    return Argument.of(Page::class.java as Class<Page<T>>, type)
}

@Suppress("UNCHECKED_CAST")
fun <T, S> searchPageOf(contactType: Class<T>, searchResultsType: Class<S>): Argument<SearchPage<T, S>> {
    return Argument.of(SearchPage::class.java as Class<SearchPage<T, S>>, contactType, searchResultsType)
}

@Suppress("UNCHECKED_CAST")
fun <T> listArgumentOf(type: Class<T>): Argument<List<T>> {
    return Argument.of(List::class.java as Class<List<T>>, type)
}
