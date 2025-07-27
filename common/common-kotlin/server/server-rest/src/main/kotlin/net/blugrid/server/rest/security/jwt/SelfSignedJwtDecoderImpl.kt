package net.blugrid.server.rest.security.jwt

import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import net.blugrid.security.tokens.model.SelfSignedJwtDecoder
import net.blugrid.platform.logging.logger
import java.security.interfaces.RSAPublicKey
import java.util.Optional

@Singleton
@Requires(property = "micronaut.security.token.jwt.signatures.jwks-static.selfSigned.path")
class SelfSignedJwtDecoderImpl(
    @Value("\${micronaut.security.token.jwt.signatures.jwks-static.selfSigned.path}") jwksFilePath: String
) : SelfSignedJwtDecoder {
    private val log = logger()
    private val publicKey: RSAPublicKey

    init {
        val jwksLoader = JwksLoader(jwksFilePath)
        publicKey = jwksLoader.publicKey
    }

    override fun decode(token: String): Optional<JWT> {
        val jwt: SignedJWT = JWTParser.parse(token) as SignedJWT
        val verifier: JWSVerifier = RSASSAVerifier(publicKey)

        return if (jwt.verify(verifier)) {
            Optional.of(jwt)
        } else {
            log.error("JWT verification failed")
            Optional.empty()
        }
    }
}