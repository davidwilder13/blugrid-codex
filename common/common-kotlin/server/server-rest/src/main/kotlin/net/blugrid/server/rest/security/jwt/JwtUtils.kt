package net.blugrid.web.core.jwt

import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.cookie.Cookie
import io.netty.handler.codec.http.cookie.DefaultCookie
import io.netty.handler.codec.http.cookie.ServerCookieEncoder

fun <T> MutableHttpRequest<T>.applyCookies(cookies: List<Cookie>): MutableHttpRequest<T> {
    return apply {
        cookies.forEach { this.cookie(it) }
    }
}

fun DefaultCookie.toCookie() = let { defaultCookie ->
    val cookie = Cookie.of(defaultCookie.name(), defaultCookie.value())
    cookie.domain(defaultCookie.domain())
    cookie.path(defaultCookie.path())
    cookie.httpOnly(defaultCookie.isHttpOnly)
    cookie.secure(defaultCookie.isSecure)
    cookie.maxAge(defaultCookie.maxAge())
    cookie
}

fun String.toCookie(cookieName: String, ttl: Long): Cookie = this.let { value ->
    return DefaultCookie(cookieName, value)
        .apply {
            isHttpOnly = true
            setMaxAge(ttl)
            setPath("/")
        }
        .toCookie()
}

fun MutableHttpResponse<*>.clearCookies(cookies: List<String>): MutableHttpResponse<*> {
    cookies.forEach { clearCookie(it) }
    return this
}

fun MutableHttpResponse<*>.clearCookie(cookieName: String) {
    cookie(
        Cookie.of(cookieName, "")
            .maxAge(0)
            .path("/")
    )
}

fun MutableHttpResponse<*>.setCookie(cookieName: String, cookieValue: String, maxAge: Long = 300000L): MutableHttpResponse<*> {
    val cookie = DefaultCookie(cookieName, cookieValue).apply {
        isHttpOnly = true
        setMaxAge(maxAge)
        setPath("/")
    }
    val cookieHeader = ServerCookieEncoder.LAX.encode(cookie)
    headers.add("set-cookie", cookieHeader)
    return this
}

fun MutableHttpResponse<*>.applyCookies(cookies: List<DefaultCookie>): MutableHttpResponse<*> {
    cookies.forEach { cookie ->
        val cookieHeader = ServerCookieEncoder.LAX.encode(cookie)
        this.headers.add("set-cookie", cookieHeader)
    }
    return this
}
