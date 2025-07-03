package net.blugrid.api.jwt.model

import com.nimbusds.jwt.JWT

interface JwtDecoder {
    fun decode(token: String): JWT
}
