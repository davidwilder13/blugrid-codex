package net.blugrid.security.core.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("security")
interface SecurityProps {

    @ConfigurationProperties("auth0")
    class Auth0Config {
        lateinit var audience: String
        lateinit var auth0Domain: String
        lateinit var clientId: String
    }

    @ConfigurationProperties("cookies")
    interface CookieConfig {

        @get:Bindable(defaultValue = "JWT")
        val jwt: String

        @get:Bindable(defaultValue = "OAUTH2_PKCE")
        val oauthPkce: String

        @get:Bindable(defaultValue = "OAUTH2_STATE")
        val oathState: String

        @get:Bindable(defaultValue = "OPENID_NONCE")
        val oathNonce: String

        @get:Bindable(defaultValue = "300000L")
        val maxAge: Long
    }
}
