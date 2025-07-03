package net.blugrid.api.security.authentication.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
import com.nimbusds.jwt.JWT
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.session.SessionType
import java.util.Date

@Schema(description = "Decorated Authentication")
@JsonTypeInfo(use = NAME, property = "authenticationType")
@JsonSubTypes(
    Type(value = GuestAuthentication::class, name = "GUEST"),
    Type(value = TenantAuthentication::class, name = "TENANT"),
    Type(value = BusinessUnitAuthentication::class, name = "BUSINESS_UNIT"),
)
abstract class DecoratedAuthentication(
    open val authenticationType: AuthenticationType,
    //sso
    open val providerId: String,
    open val principalName: String,
    open val principalEmail: String,

    // session
    open val sessionId: String,
    open val userId: String,
    open val expirationTime: Date? = null,

    // user
    open val user: AuthenticatedUser,

) : Authentication {

    var token: JWT? = null

    override fun getName(): String? {
        return principalName
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return mutableMapOf()
    }

    val isExpired: Boolean
        get() = expirationTime?.before(Date()) ?: false
}

data class GuestAuthentication(
    override val providerId: String,
    override val principalName: String,
    override val principalEmail: String,
    override val sessionId: String,
    override val userId: String,
    override val expirationTime: Date? = null,
    override val user: AuthenticatedUser,

    val webApplicationId: String,

    val session: AuthenticatedGuestSession,
): DecoratedAuthentication(
    authenticationType = AuthenticationType.GUEST,
    providerId = principalName,
    principalName = principalName,
    principalEmail = principalEmail,
    sessionId = sessionId,
    userId = userId,
    user = user,
    expirationTime = expirationTime,
)

data class TenantAuthentication(
    override val providerId: String,
    override val principalName: String,
    override val principalEmail: String,
    override val sessionId: String,
    override val userId: String,
    override val expirationTime: Date? = null,

    val tenantId: String,
    val webApplicationId: String,

    val organisation: AuthenticatedOrganisation,
    val session: AuthenticatedWebApplicationSession,
    override val user: AuthenticatedUser,
): DecoratedAuthentication(
    authenticationType = AuthenticationType.TENANT,
    providerId = principalName,
    principalName = principalName,
    principalEmail = principalEmail,
    sessionId = sessionId,
    userId = userId,
    user = user,
    expirationTime = expirationTime,
)

data class BusinessUnitAuthentication(
    override val providerId: String,
    override val principalName: String,
    override val principalEmail: String,
    override val sessionId: String,
    override val userId: String,
    override val expirationTime: Date? = null,

    val tenantId: String,
    val webApplicationId: String,
    val businessUnitId: String,

    val organisation: AuthenticatedOrganisation,
    val session: AuthenticatedBusinessUnitSession,
    override val user: AuthenticatedUser,
): DecoratedAuthentication(
    authenticationType = AuthenticationType.BUSINESS_UNIT,
    providerId = principalName,
    principalName = principalName,
    principalEmail = principalEmail,
    sessionId = sessionId,
    userId = userId,
    user = user,
    expirationTime = expirationTime,
)

data class AuthenticatedOrganisation(
    val tenantId: String,
    val primaryPartyId: String? = null,
    val displayName: String? = null,
    val partyId: String? = null
)

@Schema(description = "Session web token type")
@JsonTypeInfo(use = NAME, property = "sessionType")
@JsonSubTypes(
    Type(value = AuthenticatedGuestSession::class, name = "GUEST"),
    Type(value = AuthenticatedWebApplicationSession::class, name = "WEB_APPLICATION"),
    Type(value = AuthenticatedBusinessUnitSession::class, name = "BUSINESS_UNIT"),
)
abstract class AuthenticatedSession(
    open val sessionId: String,
    open val sessionType: SessionType,
    open val userId: String,
    open val webApplicationId: String,
)

data class AuthenticatedGuestSession(
    override val sessionId: String,
    override val userId: String,
    override val webApplicationId: String,
) : AuthenticatedSession(
    sessionId = sessionId,
    sessionType = SessionType.GUEST,
    userId = userId,
    webApplicationId = webApplicationId,
)

data class AuthenticatedWebApplicationSession(
    override val sessionId: String,
    override val userId: String,
    override val webApplicationId: String,
    val tenantId: String,
    val operatorId: String
) : AuthenticatedSession(
    sessionId = sessionId,
    sessionType = SessionType.WEB_APPLICATION,
    userId = userId,
    webApplicationId = webApplicationId,
)

class AuthenticatedBusinessUnitSession(
    override val sessionId: String,
    override val userId: String,
    override val webApplicationId: String,
    val tenantId: String,
    val operatorId: String,
    val businessUnitId: String,
) : AuthenticatedSession(
    sessionId = sessionId,
    sessionType = SessionType.BUSINESS_UNIT,
    userId = userId,
    webApplicationId = webApplicationId,
)

data class AuthenticatedUser(
    val userIdentityId: String,
    val partyId: String? = null,
    val tenantId: String? = null,
    val displayName: String? = null,
    val email: String,
    val emailVerified: Boolean? = true,
    val nickName: String? = null,
    val givenName: String? = null,
    val familyName: String? = null,
    val pictureUrl: String? = null,
    val providerId: String,
)

enum class AuthenticationType {
    @JsonProperty("GUEST") GUEST,
    @JsonProperty("TENANT") TENANT,
    @JsonProperty("BUSINESS_UNIT") BUSINESS_UNIT,
}

