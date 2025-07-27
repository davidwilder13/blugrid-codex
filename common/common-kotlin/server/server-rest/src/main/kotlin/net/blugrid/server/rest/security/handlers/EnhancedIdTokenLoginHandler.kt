package net.blugrid.server.rest.security.handlers

import io.micronaut.context.annotation.Replaces
import io.micronaut.http.MutableHttpResponse
import io.micronaut.security.oauth2.endpoint.token.response.IdTokenLoginHandler
import jakarta.inject.Singleton
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.DecoratedAuthentication
import net.blugrid.server.rest.security.service.CookieService
import net.blugrid.server.rest.security.service.RedirectService
import java.util.UUID

@Singleton
@Replaces(IdTokenLoginHandler::class)
class EnhancedIdTokenLoginHandler(
    private val cookieService: CookieService,
    private val redirectService: RedirectService,
) {

    fun tenantLoginSuccess(multitenantAuthentication: DecoratedAuthentication<BaseAuthenticatedSession>): MutableHttpResponse<*> {
        return cookieService.applyJwtCookie(
            response = redirectService.redirectToLoginSuccess(),
            authentication = multitenantAuthentication
        )
    }

    fun invitationLoginSuccess(multitenantAuthentication: DecoratedAuthentication<BaseAuthenticatedSession>, partyRegistrationInvitationUuid: UUID): MutableHttpResponse<*> {
        return cookieService.applyJwtCookie(
            response = redirectService.redirectToLoginRegistration(partyRegistrationInvitationUuid),
            authentication = multitenantAuthentication
        )
    }

    fun loginFailed(): MutableHttpResponse<*> {
        return cookieService.clearCookies(
            response = redirectService.redirectToLoginFailure()
        )
    }
}
