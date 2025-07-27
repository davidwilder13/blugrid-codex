@file:Suppress("DEPRECATION")

package net.blugrid.server.rest.security.jwt

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.RemoteJWKSet
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import com.nimbusds.jwt.proc.JWTProcessor
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment
import io.micronaut.security.authentication.AuthorizationException
import io.micronaut.security.oauth2.configuration.OauthClientConfigurationProperties
import jakarta.inject.Singleton
import net.blugrid.security.tokens.model.JwtDecoder
import net.blugrid.platform.logging.logger
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.ParseException
import java.util.Base64

// Not used. The Micronuat JwtValidator does this via configuration
@Singleton
@Requires(notEnv = [Environment.TEST])
class JwtDecoderImpl(
    private val oauthClientProps: OauthClientConfigurationProperties
) : JwtDecoder {

    private val log = logger()

    companion object {
        private var exactClaims: JWTClaimsSet? = null
        private var requiredClaims: Set<String>? = null
        private lateinit var processor: JWTProcessor<SecurityContext>
        private lateinit var jwksUri: String

        fun initialize(oauthClientProps: OauthClientConfigurationProperties) {
            jwksUri = oauthClientProps.openid.flatMap { it.jwksUri }.orElseThrow {
                IllegalStateException("jwksUri is not available")
            }
            processor = DefaultJWTProcessor<SecurityContext>().apply {
                jwsTypeVerifier = DefaultJOSEObjectTypeVerifier(JOSEObjectType("jwt"))
                jwsKeySelector = JWSVerificationKeySelector(
                    JWSAlgorithm.RS256,
                    RemoteJWKSet(URL(jwksUri))
                )
                jwtClaimsSetVerifier = DefaultJWTClaimsVerifier(exactClaims, requiredClaims)
            }
        }
    }

    init {
        initialize(oauthClientProps)
    }

    override fun decode(token: String): JWT {
        return try {
            processor.process(token, null)
                .let { JWTParser.parse(token) }
        } catch (e: ParseException) {
            log.error("Parsing error: $e")
            throw AuthorizationException(null)
        }
    }
}

@Singleton
@Requires(env = [Environment.TEST])
class FakeJwtDecoder : JwtDecoder {

    override fun decode(token: String): JWT {
        // Decode the token from Base64
        val decodedToken = String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8)
        val signedJWT = SignedJWT.parse(decodedToken)

        return signedJWT
    }
}