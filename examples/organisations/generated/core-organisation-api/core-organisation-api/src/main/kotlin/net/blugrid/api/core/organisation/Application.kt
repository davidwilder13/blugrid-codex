package net.blugrid.api.core.organisation

import io.micronaut.runtime.Micronaut.run
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "Organisation Core Rest API",
        version = "0.0",
        description = "Organisation Core ",
    )
)
object Application

fun main(args: Array<String>) {
    run(*args)
}
