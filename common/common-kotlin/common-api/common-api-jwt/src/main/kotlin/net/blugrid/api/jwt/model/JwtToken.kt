package net.blugrid.api.jwt.model

import net.blugrid.api.jwt.mapping.jwtObjectMapper
import net.blugrid.api.security.model.AuthenticatedOrganisation
import net.blugrid.api.security.model.AuthenticatedUser
import net.blugrid.api.security.model.AuthenticationType
import net.blugrid.api.security.model.BaseAuthenticatedSession
import net.blugrid.api.session.model.BusinessUnitSession
import net.blugrid.api.session.model.SessionType
import net.blugrid.api.session.model.TenantSession
import java.util.Date

data class JwtToken(
    val authenticationType: AuthenticationType,
    val user: AuthenticatedUser,
    val organisation: AuthenticatedOrganisation? = null,
    val session: BaseAuthenticatedSession,
    val expirationTime: Date? = null,
    val roles: MutableList<String> = mutableListOf(),
    val attributes: MutableMap<String, Any> = mutableMapOf()
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

    val tenantId: String?
        get() = when (authenticationType) {
            AuthenticationType.BUSINESS_UNIT -> (session as BusinessUnitSession).tenantId
            AuthenticationType.TENANT -> (session as TenantSession).tenantId
            else -> null
        }
    val businessUnitId: String?
        get() = if (authenticationType === AuthenticationType.BUSINESS_UNIT) {
            (session as BusinessUnitSession).businessUnitId
        } else {
            null
        }
    val sessionId: String
        get() = session.sessionId
    val sessionType: SessionType
        get() = session.sessionType
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> T.toJWTRawMap(): Map<out String, Any> =
    jwtObjectMapper.convertValue(this, Map::class.java) as Map<out String, Any>

