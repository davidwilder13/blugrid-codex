package net.blugrid.server.rest.security.filters

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.http.HttpRequest
import io.micronaut.http.server.util.HttpHostResolver
import io.micronaut.http.server.util.locale.HttpLocaleResolver
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.event.TokenValidatedEvent
import io.micronaut.security.filters.AuthenticationFetcher
import io.micronaut.security.filters.SecurityFilter
import io.micronaut.security.token.TokenAuthenticationFetcher
import jakarta.inject.Named
import jakarta.inject.Singleton
import net.blugrid.api.security.config.SecurityProps
import net.blugrid.security.authentication.jwt.toMultitenantAuthentication
import net.blugrid.security.tokens.model.SelfSignedJwtDecoder
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Replaces(TokenAuthenticationFetcher::class)
@Singleton
class JwtAuthenticationFetcher(
    private val cookieProps: SecurityProps.CookieConfig,
    private val selfSignedJwtDecoder: SelfSignedJwtDecoder,
    private val tokenValidatedEventPublisher: ApplicationEventPublisher<TokenValidatedEvent>,
    private val httpHostResolver: HttpHostResolver,
    private val httpLocaleResolver: HttpLocaleResolver,
    @Named("jwtObjectMapper") private val jwtObjectMapper: ObjectMapper
) : AuthenticationFetcher<HttpRequest<*>> {

    private val log = LoggerFactory.getLogger(JwtAuthenticationFetcher::class.java)

    companion object {
        const val ORDER = 0
    }

    override fun fetchAuthentication(request: HttpRequest<*>): Publisher<Authentication> {
        val jwtCookie = request.cookies.get(cookieProps.jwt)
        return if (jwtCookie == null) {
            Flux.empty()
        } else {
            Flux.fromIterable(listOf(jwtCookie.value))
                .flatMap { tokenValue ->
                    val jwtOpt = selfSignedJwtDecoder.decode(tokenValue)
                    if (jwtOpt.isPresent) {
                        val jwt = jwtOpt.get()

                        jwt.toMultitenantAuthentication(log, jwtObjectMapper)
                            ?.let { authentication ->
                                request.setAttribute(SecurityFilter.TOKEN, tokenValue)
                                request.setAttribute("jwt", jwt)

                                tokenValidatedEventPublisher.publishEvent(
                                    TokenValidatedEvent(
                                        tokenValue,
                                        httpHostResolver.resolve(request),
                                        httpLocaleResolver.resolveOrDefault(request)
                                    )
                                )
                                Mono.just(authentication)
                            }
                            ?: Flux.empty()
                    } else {
                        Flux.empty()
                    }
                }
        }
    }

    override fun getOrder(): Int {
        return ORDER
    }
}


