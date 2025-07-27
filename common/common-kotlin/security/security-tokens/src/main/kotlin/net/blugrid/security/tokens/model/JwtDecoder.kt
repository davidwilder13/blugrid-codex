package net.blugrid.security.tokens.model

import com.nimbusds.jwt.JWT

interface JwtDecoder {
    fun decode(token: String): JWT
}