package net.blugrid.api.security.controller

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import net.blugrid.server.rest.security.service.RedirectService

@Controller("/redirect")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
class RedirectController(
    private val redirectService: RedirectService,
) {

    @Get("/login")
    fun login(request: HttpRequest<*>): HttpResponse<*> {
        return redirectService.redirectToAuth0Login(request)
    }
}
