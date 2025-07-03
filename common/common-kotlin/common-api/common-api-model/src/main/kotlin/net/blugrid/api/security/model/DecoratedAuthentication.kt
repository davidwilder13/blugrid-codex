package net.blugrid.api.security.model

import java.util.Date

interface DecoratedAuthentication<T : BaseAuthenticatedSession> {
    val authenticationType: AuthenticationType

    // SSO core
    val providerId: String
    val principalName: String
    val principalEmail: String

    // Session
    val sessionId: String
    val userId: String
    val expirationTime: Date?

    // User + Session object
    val user: BaseAuthenticatedUser
    val session: T

    val isExpired: Boolean get() = expirationTime?.before(Date()) ?: false
}
