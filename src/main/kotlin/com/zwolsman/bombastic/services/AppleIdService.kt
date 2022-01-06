package com.zwolsman.bombastic.services

import io.netty.handler.ssl.PemPrivateKey
import org.jose4j.jwk.HttpsJwks
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.security.PrivateKey

@Service
class AppleIdService(
    private val config: Configuration,
    @Value("classpath:apple/cert.p8")
    private val resource: Resource
) {

    @ConfigurationProperties(prefix = "apple")
    @ConstructorBinding
    data class Configuration(val clientId: String, val teamId: String, val keyId: String)

    private val log = LoggerFactory.getLogger(javaClass)

    private val httpsJwks = HttpsJwks("https://appleid.apple.com/auth/keys")
    private val httpsJwksKeyResolver = HttpsJwksVerificationKeyResolver(httpsJwks)
    private val jwtConsumer = JwtConsumerBuilder()
        .setVerificationKeyResolver(httpsJwksKeyResolver)
        .setExpectedIssuer("https://appleid.apple.com")
        .setExpectedAudience(config.clientId)
        .build()

    private val privateKey: PrivateKey by lazy { PemPrivateKey.valueOf(resource.inputStream.readAllBytes()) }
    private fun createSecret(): String {
        val jws = JsonWebSignature()

        jws.algorithmHeaderValue = AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256
        jws.keyIdHeaderValue = config.keyId

        val claims = JwtClaims()
        claims.issuer = config.teamId
        claims.setIssuedAtToNow()
        claims.setExpirationTimeMinutesInTheFuture(60 * 24f)
        claims.audience = listOf("https://appleid.apple.com")
        claims.subject = config.clientId

        jws.payload = claims.toJson()
        jws.key = privateKey

        return jws.compactSerialization
    }

    fun verify(identityToken: String, authCode: String): Boolean {
        jwtConsumer.processToClaims(identityToken)
        val secret = createSecret()

        return true
    }
}