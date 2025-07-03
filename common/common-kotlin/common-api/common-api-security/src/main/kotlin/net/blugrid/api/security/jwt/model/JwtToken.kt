package net.blugrid.api.security.jwt.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.blugrid.api.common.model.session.SessionType
import net.blugrid.api.security.authentication.model.AuthenticatedBusinessUnitSession
import net.blugrid.api.security.authentication.model.AuthenticatedOrganisation
import net.blugrid.api.security.authentication.model.AuthenticatedSession
import net.blugrid.api.security.authentication.model.AuthenticatedUser
import net.blugrid.api.security.authentication.model.AuthenticatedWebApplicationSession
import net.blugrid.api.security.authentication.model.AuthenticationType
import java.util.Date

data class JwtToken(
    val authenticationType: AuthenticationType,
    val user: AuthenticatedUser,
    val organisation: AuthenticatedOrganisation? = null,
    val session: AuthenticatedSession,
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
        get() =  when (authenticationType) {
            AuthenticationType.BUSINESS_UNIT -> (session as AuthenticatedBusinessUnitSession).tenantId
            AuthenticationType.TENANT ->  (session as AuthenticatedWebApplicationSession).tenantId
            else -> null
        }
    val businessUnitId: String?
        get() = if (authenticationType === AuthenticationType.BUSINESS_UNIT) {
            (session as AuthenticatedBusinessUnitSession).businessUnitId
        } else {
            null
        }
    val sessionId: String
        get() = session.sessionId
    val sessionType: SessionType
        get() = session.sessionType
}


val jwtObjectMapper: ObjectMapper = with(ObjectMapper()) {
    propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    registerModule(KotlinModule.Builder().build())
    registerModule(JavaTimeModule())
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> T.toJWTRawMap(): Map<out String, Any> =
    jwtObjectMapper.convertValue(this, Map::class.java) as Map<out String, Any>

