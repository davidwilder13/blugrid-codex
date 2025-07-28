package net.blugrid.platform.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("env.db")
interface DbProps {

    @get:Bindable(defaultValue = "")
    val dbname: String

    @get:Bindable(defaultValue = "public")
    val schema: String
}
