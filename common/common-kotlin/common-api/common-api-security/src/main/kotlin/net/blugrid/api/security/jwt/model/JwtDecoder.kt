package net.blugrid.api.security.jwt.model

import com.nimbusds.jwt.JWT

interface JwtDecoder {
    fun decode(token: String): JWT
}
