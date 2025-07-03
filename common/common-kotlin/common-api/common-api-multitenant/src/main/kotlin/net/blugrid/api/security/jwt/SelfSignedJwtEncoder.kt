package net.blugrid.api.security.jwt

import jakarta.inject.Singleton
import net.blugrid.api.security.jwt.factory.AccessTokenFactory
import net.blugrid.api.security.jwt.model.JwtToken

@Singleton
class SelfSignedJwtEncoder {
    fun encode(token: JwtToken): String {
        return AccessTokenFactory.token(token)
    }
}
