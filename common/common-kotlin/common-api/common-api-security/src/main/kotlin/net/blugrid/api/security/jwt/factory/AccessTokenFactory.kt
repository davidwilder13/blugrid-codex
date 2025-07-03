package net.blugrid.api.security.jwt.factory

import net.blugrid.api.security.jwt.model.JwtToken
import net.blugrid.api.security.jwt.model.toJWTRawMap

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

