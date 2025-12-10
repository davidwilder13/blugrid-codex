package net.blugrid.api.core.organisation.graphql

import io.micronaut.runtime.Micronaut

fun main(args: Array<String>) {
    Micronaut.build(*args)
        .eagerInitSingletons(true)
        .mainClass(Application::class.java)
        .start()
}

object Application
