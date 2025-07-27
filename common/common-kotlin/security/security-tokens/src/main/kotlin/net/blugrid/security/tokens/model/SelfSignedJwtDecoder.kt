package net.blugrid.security.tokens.model

import com.nimbusds.jwt.JWT
import java.util.Optional

interface SelfSignedJwtDecoder {
    fun decode(token: String): Optional<JWT>
}