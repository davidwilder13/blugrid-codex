package net.blugrid.platform.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("env.server")
interface ServerProps {

    @get:Bindable(defaultValue = "")
    val baseUri: String
}