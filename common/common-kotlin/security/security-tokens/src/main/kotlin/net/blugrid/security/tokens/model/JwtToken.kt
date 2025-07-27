package net.blugrid.security.tokens.model

import net.blugrid.security.core.model.AuthenticatedOrganisation
import net.blugrid.security.core.model.AuthenticatedUser
import net.blugrid.security.core.model.AuthenticationType
import net.blugrid.security.core.model.BaseAuthenticatedSession
import java.util.Date

data class JwtToken(
    val authenticationType: AuthenticationType,
    val user: AuthenticatedUser,
    val organisation: AuthenticatedOrganisation? = null,
    val session: BaseAuthenticatedSession,
    val expirationTime: Date? = null,
    val roles: MutableList<String> = mutableListOf(),
    val attributes: MutableMap<String, Any> = mutableMapOf(),
    val tenantId: String? = null,
    val businessUnitId: String? = null
) {
    val name: String?
        get() = user.displayName
    val providerId: String
        get() = user.providerId
    val principalName: String?
        get() = user.displayName
    val principalEmail: String
        get() = user.email
    val userIdentityId: String
        get() = user.userIdentityId
    val sessionId: String
        get() = session.sessionId
}