package net.blugrid.api.security.mapping

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jwt.JWT
import io.micronaut.security.authentication.Authentication
import net.blugrid.api.jwt.model.JwtToken
import net.blugrid.api.security.model.AuthenticationType
import org.slf4j.Logger

fun JWT.toMultitenantAuthentication(
    log: Logger,
    objectMapper: ObjectMapper
): Authentication? =
    try {
        val rootNode: JsonNode = objectMapper.readTree(jwtClaimsSet.toString())
        val authType = objectMapper.readValue(
            rootNode.get("authentication_type").toString(),
            AuthenticationType::class.java
        )
        val jwtToken = objectMapper.treeToValue(rootNode, JwtToken::class.java)
        jwtToken.toAuthentication(authType)
    } catch (e: Exception) {
        log.error("JWT decode failed: ${e.message}", e)
        null
    }
