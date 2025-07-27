package net.blugrid.api.security.controller

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import net.blugrid.server.rest.security.service.RedirectService
import toMultitenantAuthentication

@Controller("/check-session")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
class CheckSessionController(
    private val redirectService: RedirectService
) {

    @Get("/")
    @Produces(MediaType.APPLICATION_JSON)
    fun checkSession(request: HttpRequest<*>): HttpResponse<*> {
        val authenticationOpt = request.toMultitenantAuthentication()
        return if (authenticationOpt.isPresent) {
            return if (authenticationOpt.get().isExpired) {
                sendFailedResponse()
            } else {
                HttpResponse.ok<Any>()
            }
        } else {
            sendFailedResponse()
        }
    }

    private fun sendFailedResponse() = HttpResponse.unauthorized<Map<String, String>>()
        .body(mapOf("redirectUri" to redirectService.getAuth0AuthorizeCallback.toString()))
}
