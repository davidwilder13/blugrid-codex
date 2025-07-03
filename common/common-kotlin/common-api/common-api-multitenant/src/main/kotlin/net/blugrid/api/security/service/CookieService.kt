package net.blugrid.api.security.service

import io.micronaut.http.MutableHttpResponse
import jakarta.inject.Singleton
import net.blugrid.api.logging.logger
import net.blugrid.api.security.authentication.model.DecoratedAuthentication
import net.blugrid.api.security.config.SecurityProps
import net.blugrid.api.security.jwt.SelfSignedJwtEncoder
import net.blugrid.api.security.jwt.clearCookies
import net.blugrid.api.security.jwt.mapping.toJwtToken
import net.blugrid.api.security.jwt.setCookie
import java.time.Duration
import java.time.Instant

interface CookieService {
    fun applyJwtCookie(response: MutableHttpResponse<*>, authentication: DecoratedAuthentication): MutableHttpResponse<*>
    fun applyStateCookie(response: MutableHttpResponse<*>, state: String): MutableHttpResponse<*>
    fun clearCookies(response: MutableHttpResponse<*>): MutableHttpResponse<*>
    fun applyNonceCookie(response: MutableHttpResponse<*>, nonce: String): MutableHttpResponse<*>
}

@Singleton
class CookieServiceImpl(
    private val cookieSecurityProps: SecurityProps.CookieConfig,
    private val selfSignedJwtEncoder: SelfSignedJwtEncoder
) : CookieService {
    private val log = logger()

    override fun applyJwtCookie(response: MutableHttpResponse<*>, authentication: DecoratedAuthentication): MutableHttpResponse<*> {
        val ttl = authentication.toCookieExpiration().toMillis()
        val jwt = authentication.toJwtToken()
        val accessToken = selfSignedJwtEncoder.encode(jwt)
        return response.setCookie(cookieSecurityProps.jwt, accessToken, ttl)
    }

    override fun applyNonceCookie(response: MutableHttpResponse<*>, nonce: String): MutableHttpResponse<*> {
        return response.setCookie(cookieSecurityProps.oathNonce, nonce)
    }

    override fun applyStateCookie(response: MutableHttpResponse<*>, state: String): MutableHttpResponse<*> {
        return response.setCookie(cookieSecurityProps.oathState, state)
    }

    override fun clearCookies(response: MutableHttpResponse<*>): MutableHttpResponse<*> {
        return response.clearCookies(listOf(
            cookieSecurityProps.jwt,
            cookieSecurityProps.oauthPkce,
            cookieSecurityProps.oathState,
            cookieSecurityProps.oathNonce
        ))
    }

    private fun DecoratedAuthentication.toCookieExpiration(): Duration =
        expirationTime?.let {
            Duration.between(Instant.now(), it.toInstant())
        } ?: Duration.ofDays(1)
}
