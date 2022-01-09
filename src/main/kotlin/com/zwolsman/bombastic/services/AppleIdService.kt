package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.config.AppleIdConfiguration
import com.zwolsman.bombastic.repositories.appleId.AppleIdRepository
import com.zwolsman.bombastic.repositories.appleId.TokenResponse
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.jose4j.jwk.HttpsJwks
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver
import org.springframework.stereotype.Service
import java.security.PrivateKey
import kotlin.io.path.Path
import kotlin.io.path.reader

@Service
class AppleIdService(
    private val config: AppleIdConfiguration,
    private val repo: AppleIdRepository,
) {

    private val httpsJwks = HttpsJwks("https://appleid.apple.com/auth/keys")
    private val httpsJwksKeyResolver = HttpsJwksVerificationKeyResolver(httpsJwks)
    private val jwtConsumer = JwtConsumerBuilder()
        .setVerificationKeyResolver(httpsJwksKeyResolver)
        .setExpectedIssuer("https://appleid.apple.com")
        .setExpectedAudience(config.clientId)
        .build()

    private val privateKey: PrivateKey by lazy {
        val parser = PEMParser(Path(config.certificatePath).reader())
        val converter = JcaPEMKeyConverter()
        val info = parser.readObject() as PrivateKeyInfo
        converter.getPrivateKey(info)
    }

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

    suspend fun verify(identityToken: String, authCode: String): TokenResponse {
        jwtConsumer.processToClaims(identityToken)
        val secret = createSecret()
        return repo.validateCode(secret, authCode)
    }

    fun userId(identityToken: String): String {
        return jwtConsumer.processToClaims(identityToken).subject
    }
}
