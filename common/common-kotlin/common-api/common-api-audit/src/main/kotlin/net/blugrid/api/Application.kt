package net.blugrid.api

import io.micronaut.runtime.Micronaut.run
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "Audit API",
        version = "0.0",
        description = "Common Audit API",
    )
)
object Application

fun main(args: Array<String>) {
    run(*args)
}
