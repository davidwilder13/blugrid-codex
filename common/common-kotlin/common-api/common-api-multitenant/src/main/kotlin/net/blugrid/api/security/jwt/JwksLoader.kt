package net.blugrid.api.security.jwt

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec

class JwksLoader(jwksFilePath: String) {
    val publicKey: RSAPublicKey

    init {
        val jwksStream = javaClass.classLoader.getResourceAsStream(jwksFilePath)
            ?: throw IllegalArgumentException("Cannot load file: $jwksFilePath")
        val jwks = jwksStream.bufferedReader().use { it.readText() }
        val jwkSet = JWKSet.parse(jwks)
        val jwk = jwkSet.keys.first() as RSAKey
        val modulus = jwk.modulus.decodeToBigInteger()
        val exponent = jwk.publicExponent.decodeToBigInteger()
        val keySpec = RSAPublicKeySpec(modulus, exponent)
        val keyFactory = KeyFactory.getInstance("RSA")
        publicKey = keyFactory.generatePublic(keySpec) as RSAPublicKey
    }
}
