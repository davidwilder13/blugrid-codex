package net.blugrid.api.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("env")
interface EnvProps {

    @get:Bindable(defaultValue = "")
    val name: String
}

@ConfigurationProperties("env.server")
interface ServerProps {

    @get:Bindable(defaultValue = "")
    val baseUri: String
}

@ConfigurationProperties("env.web")
interface WebProps {

    @get:Bindable(defaultValue = "")
    val baseUri: String
}


@ConfigurationProperties("env.db")
interface DbProps {

    @get:Bindable(defaultValue = "")
    val dbname: String

    @get:Bindable(defaultValue = "")
    val schema: String
}
