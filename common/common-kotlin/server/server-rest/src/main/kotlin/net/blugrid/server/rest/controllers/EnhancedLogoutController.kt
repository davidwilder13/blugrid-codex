package net.blugrid.api.security.controller

import io.micronaut.context.annotation.Replaces
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.util.HttpHostResolver
import io.micronaut.http.server.util.locale.HttpLocaleResolver
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.endpoints.LogoutController
import io.micronaut.security.event.LogoutEvent
import io.micronaut.security.rules.SecurityRule
import net.blugrid.server.rest.security.service.CookieService
import net.blugrid.server.rest.security.service.RedirectService

@Replaces(LogoutController::class)
@Controller("\${micronaut.security.endpoints.logout.path:/logout}")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
class EnhancedLogoutController(
    private val cookieService: CookieService,
    private val httpHostResolver: HttpHostResolver,
    private val httpLocaleResolver: HttpLocaleResolver,
    private val logoutEventPublisher: ApplicationEventPublisher<LogoutEvent>,
    private val redirectService: RedirectService,
) {

    @Get
    @Produces(MediaType.APPLICATION_JSON)
    fun startLogout(request: HttpRequest<*>): MutableHttpResponse<*> {
        return redirectService.redirectToAuth0Logout(request)
    }

    @Get("/callback")
    fun handleCallback(request: HttpRequest<*>, authentication: Authentication?): MutableHttpResponse<*> {
        if (authentication != null) {
            logoutEventPublisher.publishEvent(LogoutEvent(authentication, httpHostResolver.resolve(request), httpLocaleResolver.resolveOrDefault(request)))
        }
        return cookieService.clearCookies(
            response = redirectService.redirectToLogoutSuccess()
        )
    }
}


