package net.blugrid.server.rest.security.service

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Singleton
import net.blugrid.api.security.config.RedirectProps
import net.blugrid.api.security.config.SecurityProps
import net.blugrid.api.security.pkce.PkceUtil
import net.blugrid.platform.config.WebProps
import net.blugrid.platform.logging.logger
import java.net.URI
import java.util.UUID

@Singleton
class RedirectService(
    private val auth0Config: SecurityProps.Auth0Config,
    private val webProps: WebProps,
    private val redirectProps: RedirectProps,
    @Value("\${micronaut.security.endpoints.logout.path:/logout}")
    private val logoutPath: String,
) {

    private val log = logger()

    val getAuth0AuthorizeCallback: URI
        get() = URI(redirectProps.loginCallbackUrl)

    fun getAuth0LoginAndRegisterUri(state: String, nonce: String): URI {
        return UriBuilder.of(auth0Config.auth0Domain)
            .path("/authorize")
            .queryParam("audience", auth0Config.audience)
            .queryParam("scope", "openid profile email")
            .queryParam("response_type", "code")
            .queryParam("client_id", auth0Config.clientId)
            .queryParam("redirect_uri", redirectProps.registrationCallbackUrl)
            .queryParam("state", state)
            .queryParam("nonce", nonce)
            .build()
    }

    val auth0LoginUri: URI
        get() {
            val codeVerifier = PkceUtil.generateCodeVerifier()
            val codeChallenge = PkceUtil.generateCodeChallenge(codeVerifier)

            return UriBuilder.of(auth0Config.auth0Domain)
                .path("/authorize")
                .queryParam("audience", auth0Config.audience)
                .queryParam("scope", "openid profile email")
                .queryParam("response_type", "code")
                .queryParam("client_id", auth0Config.clientId)
                .queryParam("redirect_uri", redirectProps.loginCallbackUrl)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", "S256")
                .build()
        }

    val auth0LogoutUri: URI
        get() = UriBuilder.of(auth0Config.auth0Domain)
            .path("/v2/logout")
            .queryParam("client_id", auth0Config.clientId)
            .queryParam("returnTo", redirectProps.logoutCallbackUrl)
            .build()

    fun redirectToAuth0Login(request: HttpRequest<*>): MutableHttpResponse<*> {
        log.debug("redirectToAuth0Login: ${request.cookies}")
        return HttpResponse.seeOther<Any>(auth0LoginUri)
    }

    fun redirectToLoginSuccess(): MutableHttpResponse<*> {
        return HttpResponse.seeOther<Any>(URI(redirectProps.loginSuccessUrl))
    }

    fun getRegistrationUrl(partyRegistrationInvitationUuid: UUID): String {
        val baseUri = redirectProps.registrationUrl.trimEnd('/')
        return "$baseUri?invitationUuid=$partyRegistrationInvitationUuid"
    }

    fun redirectToLoginRegistration(partyRegistrationInvitationUuid: UUID): MutableHttpResponse<*> {
        return HttpResponse.seeOther<Any>(
            UriBuilder.of(webProps.baseUri)
                .path("/#/signup")
                .queryParam("invitationUuid", partyRegistrationInvitationUuid)
                .build()
        )
    }

    fun redirectToLoginFailure(): MutableHttpResponse<*> {
        return HttpResponse.seeOther<Any>(URI(redirectProps.loginFailureUrl))
    }

    fun redirectToLogout(): MutableHttpResponse<*> {
        return HttpResponse.seeOther<Any>(URI(logoutPath))
    }

    fun redirectToAuth0Logout(request: HttpRequest<*>): MutableHttpResponse<*> {
        log.debug("getLogoutRedirect: ${request.cookies}")
        return HttpResponse.seeOther<Any>(auth0LogoutUri)
    }

    fun redirectToLogoutSuccess(): MutableHttpResponse<*> {
        return HttpResponse.seeOther<Any>(URI(redirectProps.logoutUrl))
    }
}
