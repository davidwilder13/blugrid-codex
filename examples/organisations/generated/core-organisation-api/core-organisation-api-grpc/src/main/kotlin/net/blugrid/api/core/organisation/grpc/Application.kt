package net.blugrid.api.core.organisation.grpc

import io.grpc.protobuf.services.ProtoReflectionService
import io.micronaut.runtime.Micronaut

object Application

fun main(args: Array<String>) {
    Micronaut.build()
        .packages("net.blugrid.api")
        .mainClass(Application.javaClass)
        .singletons(ProtoReflectionService.newInstance())
        .start()
}
