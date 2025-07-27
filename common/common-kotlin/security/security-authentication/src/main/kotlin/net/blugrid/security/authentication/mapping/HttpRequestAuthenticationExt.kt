import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jwt.JWT
import io.micronaut.http.HttpRequest
import io.micronaut.security.filters.SecurityFilter
import net.blugrid.security.authentication.model.BusinessUnitAuthentication
import net.blugrid.security.authentication.model.GuestAuthentication
import net.blugrid.security.authentication.model.TenantAuthentication
import net.blugrid.security.core.model.AuthenticatedOrganisation
import net.blugrid.security.core.model.AuthenticatedUser
import net.blugrid.security.core.model.AuthenticationType
import net.blugrid.security.core.model.BaseAuthenticatedSession
import net.blugrid.security.core.model.DecoratedAuthentication
import net.blugrid.security.core.session.BusinessUnitSession
import net.blugrid.security.core.session.GuestSession
import net.blugrid.security.core.session.TenantSession
import org.slf4j.Logger
import java.time.Instant
import java.util.Date
import java.util.Optional

@Suppress("UNCHECKED_CAST")
fun HttpRequest<*>.toMultitenantAuthentication(): Optional<DecoratedAuthentication<out BaseAuthenticatedSession>> {
    val attributeValue = attributes.getValue(SecurityFilter.AUTHENTICATION)
    return if (attributeValue is DecoratedAuthentication<*>) {
        Optional.of(attributeValue)
    } else {
        Optional.empty()
    }
}

fun JWT.toMultitenantAuthentication(log: Logger, objectMapper: ObjectMapper): DecoratedAuthentication<out BaseAuthenticatedSession>? =
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

fun JsonNode.toAuthenticatedGuestSession(objectMapper: ObjectMapper): GuestSession {
    val sessionNode: JsonNode = get("session")
    return objectMapper.treeToValue(sessionNode, GuestSession::class.java)
}

fun JsonNode.toAuthenticatedBusinessUnitSession(objectMapper: ObjectMapper): BusinessUnitSession {
    val sessionNode: JsonNode = get("session")
    return objectMapper.treeToValue(sessionNode, BusinessUnitSession::class.java)
}

fun JsonNode.toAuthenticatedWebApplicationSession(objectMapper: ObjectMapper): TenantSession {
    val organisationNode: JsonNode = get("session")
    return objectMapper.treeToValue(organisationNode, TenantSession::class.java)
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
