package net.blugrid.security.core.model

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

    /**
     * Additional attributes for this authentication
     * Used by context resolvers to access tenant/business unit information
     * Note: Using getAttributes() method instead of val attributes to avoid conflict with Micronaut Authentication
     */
    fun getAttributes(): Map<String, Any>

    /**
     * Check if this authentication has expired
     */
    val isExpired: Boolean
        get() = expirationTime?.let { Date().after(it) } ?: false
}
