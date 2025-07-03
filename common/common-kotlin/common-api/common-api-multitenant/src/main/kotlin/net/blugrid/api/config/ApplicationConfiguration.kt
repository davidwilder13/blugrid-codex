package net.blugrid.api.config

import io.micronaut.context.annotation.ConfigurationProperties

interface ApplicationConfiguration {
    val max: Int
}

@ConfigurationProperties("application")
class ApplicationConfigurationProperties : ApplicationConfiguration {
    private val DEFAULT_MAX = 10
    override var max = DEFAULT_MAX
}
