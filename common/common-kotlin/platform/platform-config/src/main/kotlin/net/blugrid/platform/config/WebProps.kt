package net.blugrid.platform.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("env.web")
interface WebProps {

    @get:Bindable(defaultValue = "")
    val baseUri: String
}