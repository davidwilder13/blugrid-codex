package net.blugrid.security.tokens.factory

import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader.Builder
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import net.blugrid.platform.serialization.platformObjectMapper
import java.security.KeyStore
import java.security.KeyStore.PasswordProtection


abstract class AbstractJwtGenerator(
    keystoreFile: String = "keystore.jks",
    keystorePassword: String = "password",
    keyPairAlias: String = "jwt_key"
) {

    open val baseJwtPayload: Map<String, Any> = emptyMap()

    private val jwtSigner: JWSSigner
    private var objectMapper: ObjectMapper

    init {
        val store = loadKeystore(keystoreFile, keystorePassword)
        val keyPasswordProtection = PasswordProtection(keystorePassword.toCharArray())
        val privateKeyEntry = store.getEntry(keyPairAlias, keyPasswordProtection) as KeyStore.PrivateKeyEntry

        jwtSigner = RSASSASigner(privateKeyEntry.privateKey)

        objectMapper = platformObjectMapper
    }

    private fun loadKeystore(keystoreFile: String, keystorePassword: String): KeyStore {
        val keyStore = KeyStore.getInstance("JKS")
        val keystoreStream = javaClass.classLoader.getResourceAsStream(keystoreFile)
            ?: throw IllegalArgumentException("Keystore file not found: $keystoreFile")
        keystoreStream.use {
            keyStore.load(it, keystorePassword.toCharArray())
        }
        return keyStore
    }

    protected fun generateToken(extraAttributes: Map<out String, Any>): String {
        val payloadMap = baseJwtPayload + extraAttributes
        val payload = objectMapper.writeValueAsString(payloadMap)
        val jwsHeader = Builder(JWSAlgorithm.RS256)
            .type(JOSEObjectType.JWT)
            .build()
        val jwt = SignedJWT(jwsHeader, JWTClaimsSet.parse(payload))
        jwt.sign(jwtSigner)
        return jwt.serialize()
    }
}