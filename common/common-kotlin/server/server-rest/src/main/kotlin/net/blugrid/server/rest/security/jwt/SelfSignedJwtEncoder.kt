package net.blugrid.server.rest.security.jwt

import jakarta.inject.Singleton
import net.blugrid.security.tokens.factory.AccessTokenFactory
import net.blugrid.security.tokens.model.JwtToken

@Singleton
class SelfSignedJwtEncoder {
    fun encode(token: JwtToken): String {
        return AccessTokenFactory.token(token)
    }
}