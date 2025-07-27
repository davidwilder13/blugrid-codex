package net.blugrid.platform.testing.security

import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.ClientFilterChain
import io.micronaut.http.filter.HttpClientFilter
import io.micronaut.http.filter.ServerFilterPhase
import net.blugrid.api.security.config.SecurityProps
import net.blugrid.platform.logging.logger
import net.blugrid.web.core.jwt.applyCookies
import net.blugrid.web.core.jwt.toCookie
import org.reactivestreams.Publisher

@Requires(env = [Environment.TEST])
@Filter("/**")
internal class TestAuthFilter(
    private val cookieSecurityProps: SecurityProps.CookieConfig,
) : HttpClientFilter {

    private val log = logger()

    companion object {
        var JWT_TOKEN: String = ""
    }

    override fun doFilter(request: MutableHttpRequest<*>, chain: ClientFilterChain): Publisher<out HttpResponse<*>>? {
        if (request.isServiceRequest()) {
            log.debug("Adding JWT cookies to request: $request")
            request.applyCookies(
                listOf(
                    JWT_TOKEN.toCookie(cookieSecurityProps.jwt, 3000000),
                ),
            )
        } else {
            log.debug("Skip adding JWT cookies to request: $request")
        }
        return chain.proceed(request)
    }

    override fun getOrder(): Int = ServerFilterPhase.FIRST.order()
}

private fun MutableHttpRequest<*>.isServiceRequest(): Boolean =
    attributes.getValue("micronaut.http.serviceId").let {
        it != null && it !== "consul"
    }
