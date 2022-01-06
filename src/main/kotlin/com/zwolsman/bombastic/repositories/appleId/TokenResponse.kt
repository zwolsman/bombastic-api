package com.zwolsman.bombastic.repositories.appleId

data class TokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Int,
    val refreshToken: String,
    val idToken: String,
)
