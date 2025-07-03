package net.blugrid.api.security.authentication.mapping

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jwt.JWT
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.filters.SecurityFilter
import net.blugrid.api.common.model.session.BusinessUnitSession
import net.blugrid.api.common.model.session.GuestSession
import net.blugrid.api.common.model.session.WebApplicationSession
import net.blugrid.api.common.model.user.UserIdentity
import net.blugrid.api.common.model.user.UserIdentityCreate
import net.blugrid.api.common.model.user.UserIdentityUpdate
import net.blugrid.api.security.authentication.model.AuthenticatedBusinessUnitSession
import net.blugrid.api.security.authentication.model.AuthenticatedGuestSession
import net.blugrid.api.security.authentication.model.AuthenticatedOrganisation
import net.blugrid.api.security.authentication.model.AuthenticatedUser
import net.blugrid.api.security.authentication.model.AuthenticatedWebApplicationSession
import net.blugrid.api.security.authentication.model.AuthenticationType
import net.blugrid.api.security.authentication.model.BusinessUnitAuthentication
import net.blugrid.api.security.authentication.model.DecoratedAuthentication
import net.blugrid.api.security.authentication.model.GuestAuthentication
import net.blugrid.api.security.authentication.model.TenantAuthentication
import org.slf4j.Logger
import java.time.Instant
import java.util.Date
import java.util.Optional
import java.util.UUID

fun GuestSession.toGuestAuthentication(
    userIdentity: UserIdentity,
) = let { session ->
    GuestAuthentication(
        providerId = userIdentity.providerId,
        principalName = userIdentity.name,
        principalEmail = userIdentity.email,
        sessionId = session.id.toString(),
        userId = user.id.toString(),
        webApplicationId = session.webApplication.id.toString(),

        session = AuthenticatedGuestSession(
            sessionId = session.id.toString(),
            userId = session.user.id.toString(),
            webApplicationId = session.webApplication.id.toString(),
        ),
        user = AuthenticatedUser(
            userIdentityId = userIdentity.id.toString(),
            displayName = userIdentity.displayName,
            partyId = userIdentity.partyId.toString(),
            email = userIdentity.email,
            emailVerified = userIdentity.emailVerified,
            nickName = userIdentity.nickName,
            givenName = userIdentity.givenName,
            familyName = userIdentity.familyName,
            pictureUrl = userIdentity.pictureUrl,
            providerId = userIdentity.providerId
        ),
    )
}

fun WebApplicationSession.toTenantAuthentication() = let { session ->
    TenantAuthentication(
        providerId = session.user.providerId,
        principalName = session.user.name,
        principalEmail = session.user.email,
        sessionId = session.id.toString(),
        userId = user.id.toString(),
        tenantId = organisation.id.toString(),
        webApplicationId = session.webApplication.id.toString(),

        organisation = AuthenticatedOrganisation(
            tenantId = organisation.id.toString(),
            primaryPartyId = organisation.primaryPartyId.toString(),
            displayName = organisation.displayName,
            partyId = organisation.partyId?.toString()
        ),
        session = AuthenticatedWebApplicationSession(
            sessionId = session.id.toString(),
            userId = session.user.id.toString(),
            tenantId = session.organisation.id.toString(),
            webApplicationId = session.webApplication.id.toString(),
            operatorId = operator.id.toString(),
        ),
        user = AuthenticatedUser(
            userIdentityId = session.user.id.toString(),
            displayName = session.user.displayName,
            partyId = session.user.partyId.toString(),
            email = session.user.email,
            emailVerified = session.user.emailVerified,
            nickName = session.user.nickName,
            givenName = session.user.givenName,
            familyName = session.user.familyName,
            pictureUrl = session.user.pictureUrl,
            providerId = session.user.providerId
        ),
    )
}

fun BusinessUnitSession.toBusinessUnitAuthentication() = let { session ->
    BusinessUnitAuthentication(
        providerId = session.user.providerId,
        principalName = session.user.name,
        principalEmail = session.user.email,
        sessionId = session.id.toString(),
        userId = user.id.toString(),
        tenantId = organisation.id.toString(),
        webApplicationId = session.webApplication.id.toString(),
        businessUnitId = session.businessUnitId.toString(),

        organisation = AuthenticatedOrganisation(
            tenantId = organisation.id.toString(),
            primaryPartyId = organisation.primaryPartyId.toString(),
            displayName = organisation.displayName,
            partyId = organisation.partyId?.toString()
        ),
        session = AuthenticatedBusinessUnitSession(
            sessionId = session.id.toString(),
            userId = session.user.id.toString(),
            tenantId = session.organisation.id.toString(),
            businessUnitId = session.businessUnitId.toString(),
            webApplicationId = session.webApplication.id.toString(),
            operatorId = operator.id.toString(),
        ),
        user = AuthenticatedUser(
            userIdentityId = session.user.id.toString(),
            displayName = session.user.displayName,
            partyId = session.user.partyId.toString(),
            email = session.user.email,
            emailVerified = session.user.emailVerified,
            nickName = session.user.nickName,
            givenName = session.user.givenName,
            familyName = session.user.familyName,
            pictureUrl = session.user.pictureUrl,
            providerId = session.user.providerId
        ),
    )
}

fun TenantAuthentication.toBusinessUnitAuthentication(businessUnitSession: BusinessUnitSession) = let { existing ->
    BusinessUnitAuthentication(
        providerId = existing.providerId,
        principalName = existing.principalName,
        principalEmail = existing.principalEmail,
        sessionId = existing.sessionId,
        userId = existing.userId,
        expirationTime = expirationTime,
        tenantId = existing.tenantId,
        webApplicationId = existing.webApplicationId,
        businessUnitId = businessUnitSession.businessUnitId.toString(),
        organisation = existing.organisation,
        session = businessUnitSession.toAuthenticatedBusinessUnitSession(),
        user = existing.user,
    )
}

fun BusinessUnitAuthentication.toBusinessUnitAuthentication(businessUnitSession: BusinessUnitSession) = let { existing ->
    BusinessUnitAuthentication(
        providerId = existing.providerId,
        principalName = existing.principalName,
        principalEmail = existing.principalEmail,
        sessionId = existing.sessionId,
        userId = existing.userId,
        expirationTime = expirationTime,
        tenantId = existing.tenantId,
        webApplicationId = existing.webApplicationId,
        businessUnitId = businessUnitSession.businessUnitId.toString(),
        organisation = existing.organisation,
        session = businessUnitSession.toAuthenticatedBusinessUnitSession(),
        user = existing.user,
    )
}

fun BusinessUnitSession.toAuthenticatedBusinessUnitSession() = let { session ->
    AuthenticatedBusinessUnitSession(
        sessionId = session.id.toString(),
        userId = session.user.id.toString(),
        tenantId = session.organisation.id.toString(),
        webApplicationId = session.webApplication.id.toString(),
        operatorId = operator.id.toString(),
        businessUnitId = session.businessUnitId.toString()
    )
}

fun Authentication.toUserIdentityCreate() =
    UserIdentityCreate(
        uuid = UUID.randomUUID(),
        displayName = attributes.get("name")?.toString(),
        email = attributes.get("email")?.toString() ?: "unknown",
        emailVerified = attributes.get("email_verified")?.let { it as Boolean },
        nickName = attributes.get("nickname")?.toString(),
        givenName = attributes.get("given_name")?.toString(),
        familyName = attributes.get("family_name")?.toString(),
        pictureUrl = attributes.get("picture")?.toString(),
        providerId = name
    )

fun Authentication.toUserIdentityUpdate(id: Long, uuid: UUID): UserIdentityUpdate =
    UserIdentityUpdate(
        id = id,
        uuid = uuid,
        displayName = attributes.get("name")?.toString(),
        email = attributes.get("email")?.toString() ?: "unknown",
        emailVerified = attributes.get("email_verified")?.let { it as Boolean },
        nickName = attributes.get("nickname")?.toString(),
        givenName = attributes.get("given_name")?.toString(),
        familyName = attributes.get("family_name")?.toString(),
        pictureUrl = attributes.get("picture")?.toString(),
        providerId = name
    )

fun JWT.toMultitenantAuthentication(log: Logger, objectMapper: ObjectMapper): DecoratedAuthentication? =
    try {
        val rootNode: JsonNode = objectMapper.readTree(jwtClaimsSet.toString())

        val providerId = rootNode.get("provider_id").asText()
        val principalName = rootNode.get("principal_name").asText()
        val principalEmail = rootNode.get("principal_email").asText()
        val sessionId = rootNode.get("session_id").asText()
        val userId = rootNode.get("user_identity_id").asText()
        val expirationTime = rootNode.toExpirationTime()
        val webApplicationId = rootNode.get("web_application_id")?.asText() ?: "100000001"

        val user = rootNode.toAuthenticatedUser(objectMapper)

        val authenticationType = objectMapper.readValue(
            rootNode.get("authentication_type").toString(),
            AuthenticationType::class.java
        )

        when (authenticationType!!) {
            AuthenticationType.GUEST -> GuestAuthentication(
                providerId = providerId,
                principalName = principalName,
                principalEmail = principalEmail,
                sessionId = sessionId,
                userId = userId,
                expirationTime = expirationTime,
                webApplicationId = webApplicationId,
                session = rootNode.toAuthenticatedGuestSession(objectMapper),
                user = user,
            )

            AuthenticationType.TENANT -> TenantAuthentication(
                providerId = providerId,
                principalName = principalName,
                principalEmail = principalEmail,
                sessionId = sessionId,
                userId = userId,
                expirationTime = expirationTime,
                tenantId = rootNode.get("tenant_id").asText(),
                webApplicationId = webApplicationId,
                organisation = rootNode.toAuthenticatedOrganisation(objectMapper),
                session = rootNode.toAuthenticatedWebApplicationSession(objectMapper),
                user = user,
            )

            AuthenticationType.BUSINESS_UNIT -> BusinessUnitAuthentication(
                providerId = providerId,
                principalName = principalName,
                principalEmail = principalEmail,
                sessionId = sessionId,
                userId = userId,
                expirationTime = expirationTime,
                tenantId = rootNode.get("tenant_id").asText(),
                businessUnitId = rootNode.get("business_unit_id").asText(),
                webApplicationId = webApplicationId,
                organisation = rootNode.toAuthenticatedOrganisation(objectMapper),
                session = rootNode.toAuthenticatedBusinessUnitSession(objectMapper),
                user = user,
            )
        }
    } catch (e: JsonProcessingException) {
        log.error("Error decoding authentication: ${e.message}")
        null
    }

fun JsonNode.toAuthenticatedOrganisation(objectMapper: ObjectMapper): AuthenticatedOrganisation {
    val organisationNode: JsonNode = get("organisation")
    return objectMapper.treeToValue(organisationNode, AuthenticatedOrganisation::class.java)
}

fun JsonNode.toAuthenticatedGuestSession(objectMapper: ObjectMapper): AuthenticatedGuestSession {
    val sessionNode: JsonNode = get("session")
    return objectMapper.treeToValue(sessionNode, AuthenticatedGuestSession::class.java)
}

fun JsonNode.toAuthenticatedBusinessUnitSession(objectMapper: ObjectMapper): AuthenticatedBusinessUnitSession {
    val sessionNode: JsonNode = get("session")
    return objectMapper.treeToValue(sessionNode, AuthenticatedBusinessUnitSession::class.java)
}

fun JsonNode.toAuthenticatedWebApplicationSession(objectMapper: ObjectMapper): AuthenticatedWebApplicationSession {
    val organisationNode: JsonNode = get("session")
    return objectMapper.treeToValue(organisationNode, AuthenticatedWebApplicationSession::class.java)
}

fun JsonNode.toAuthenticatedUser(objectMapper: ObjectMapper): AuthenticatedUser {
    val userNode: JsonNode = get("user")
    return objectMapper.treeToValue(userNode, AuthenticatedUser::class.java)
}

fun JsonNode.toExpirationTime(): Date? {
    val expirationTimeStr = get("expiration_time")?.asText()
    return if (expirationTimeStr != null) {
        try {
            Date.from(Instant.parse(expirationTimeStr))
        } catch (e: Exception) {
            null
        }
    } else {
        null
    }
}

fun HttpRequest<*>.toMultitenantAuthentication(): Optional<DecoratedAuthentication> {
    val attributeValue = attributes.getValue(SecurityFilter.AUTHENTICATION)
    return if (attributeValue != null) {
        Optional.of(attributeValue as DecoratedAuthentication)
    } else {
        Optional.empty()
    }
}

