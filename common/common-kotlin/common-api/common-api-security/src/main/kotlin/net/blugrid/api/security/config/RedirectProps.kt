package net.blugrid.api.security.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("redirect")
interface RedirectProps {

    val loginCallbackUrl: String
    val loginSuccessUrl: String
    val loginFailureUrl: String
    val logoutCallbackUrl: String
    val logoutUrl: String
    val registrationUrl: String
    val registrationCallbackUrl: String
}
