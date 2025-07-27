package net.blugrid.security.tokens.factory

import net.blugrid.security.tokens.model.JwtToken
import net.blugrid.security.tokens.mapping.toJWTRawMap

object AccessTokenFactory : AbstractJwtGenerator(
    keystoreFile = "keystore.jks",
    keystorePassword = "password",
    keyPairAlias = "jwt_key",
) {
    override val baseJwtPayload: Map<String, Any> = emptyMap()

    fun token(
        jwtToken: JwtToken
    ): String {
        val payloadMap = jwtToken.toJWTRawMap().toMutableMap()
        return generateToken(payloadMap)
    }
}