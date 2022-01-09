package com.zwolsman.bombastic.controllers.auth.response

import com.fasterxml.jackson.annotation.JsonProperty
import org.jose4j.jwk.JsonWebKey
import org.jose4j.jwk.RsaJsonWebKey

data class KeysResponse(val keys: List<KeyResponse>)

data class KeyResponse(
    @JsonProperty(JsonWebKey.KEY_TYPE_PARAMETER)
    val keyType: String,
    @JsonProperty(JsonWebKey.KEY_ID_PARAMETER)
    val keyId: String,
    @JsonProperty(JsonWebKey.USE_PARAMETER)
    val usage: String,
    @JsonProperty(JsonWebKey.ALGORITHM_PARAMETER)
    val algorithm: String,
    @JsonProperty(RsaJsonWebKey.MODULUS_MEMBER_NAME)
    val modulus: String,
    @JsonProperty(RsaJsonWebKey.EXPONENT_MEMBER_NAME)
    val exponent: String
)

fun KeyResponse(jsonWebKey: JsonWebKey): KeyResponse {
    val params = jsonWebKey.toParams(JsonWebKey.OutputControlLevel.PUBLIC_ONLY)
    return KeyResponse(
        keyType = jsonWebKey.keyType,
        keyId = jsonWebKey.keyId,
        usage = jsonWebKey.use,
        algorithm = jsonWebKey.algorithm,
        modulus = params[RsaJsonWebKey.MODULUS_MEMBER_NAME].toString(),
        exponent = params[RsaJsonWebKey.EXPONENT_MEMBER_NAME].toString()
    )
}
