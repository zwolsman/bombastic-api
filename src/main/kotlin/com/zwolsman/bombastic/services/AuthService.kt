package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.domain.Profile
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jwk.JsonWebKey
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.security.KeyPair

@Service
class AuthService(
    private val appleIdService: AppleIdService,
    private val profileService: ProfileService,
    @Value("classpath:certs/private.pem")
    privateCert: Resource,
) {
    private val issuer = "bombastic.dev"
    private val audience = "dev.bombastic.app"

    private val keys = run {
        val parser = PEMParser(privateCert.inputStream.reader())
        val converter = JcaPEMKeyConverter()
        val keys = parser.readObject() as PEMKeyPair

        val publicKey = converter.getPublicKey(keys.publicKeyInfo)
        val privateKey = converter.getPrivateKey(keys.privateKeyInfo)

        KeyPair(publicKey, privateKey)
    }

    private val rsaJsonWebKey = JsonWebKey.Factory.newJwk(keys.public).apply {
        keyId = "key-id"
    }

    private val jwtConsumer = JwtConsumerBuilder()
        .setRequireSubject()
        .setExpectedIssuer(issuer)
        .setExpectedAudience(audience)
        .setVerificationKey(keys.public)
        .setJwsAlgorithmConstraints(
            AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256
        )
        .build()

    private fun createAccessToken(profile: Profile): String {
        val claims = JwtClaims()
        claims.issuer = issuer

        claims.setAudience(audience)
        claims.setGeneratedJwtId()
        claims.setIssuedAtToNow()
        claims.subject = profile.id.toString()

        claims.setClaim("email", profile.email)
        val jws = JsonWebSignature()
        jws.payload = claims.toJson()
        jws.key = keys.private
        jws.keyIdHeaderValue = rsaJsonWebKey.keyId
        jws.algorithmHeaderValue = AlgorithmIdentifiers.RSA_USING_SHA256

        return jws.compactSerialization
    }

    suspend fun signUp(email: String, fullName: String, authCode: String, identityToken: String): String {
        val appleTokens = appleIdService.verify(identityToken, authCode)
        val appleUserId = appleIdService.userId(appleTokens.idToken)
        val profile = profileService.createAppleUser(
            fullName,
            email,
            appleUserId,
            appleTokens.refreshToken,
            appleTokens.accessToken
        )

        return createAccessToken(profile)
    }

    fun claims(token: String): JwtClaims {
        return jwtConsumer.processToClaims(token)
    }

    suspend fun verify(authCode: String, identityToken: String): String {
        val appleTokens = appleIdService.verify(identityToken, authCode)
        val appleUserId = appleIdService.userId(appleTokens.idToken)
        val profile = profileService.findByAppleUserId(appleUserId)

        return createAccessToken(profile)
    }
}
