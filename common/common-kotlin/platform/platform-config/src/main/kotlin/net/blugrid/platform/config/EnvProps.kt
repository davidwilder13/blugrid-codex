package net.blugrid.platform.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("env")
interface EnvProps {

    @get:Bindable(defaultValue = "")
    val name: String
}