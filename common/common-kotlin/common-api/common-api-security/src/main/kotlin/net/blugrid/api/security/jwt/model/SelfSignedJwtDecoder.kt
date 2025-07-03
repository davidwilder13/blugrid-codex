package net.blugrid.api.security.jwt.model

import com.nimbusds.jwt.JWT
import java.util.Optional

interface SelfSignedJwtDecoder {
    fun decode(token: String): Optional<JWT>
}
