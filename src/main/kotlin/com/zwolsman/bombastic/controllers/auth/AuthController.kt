package com.zwolsman.bombastic.controllers.auth

import com.zwolsman.bombastic.services.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/sign-up")
    suspend fun signUp(@RequestBody payload: SignUpPayload) =
        authService
            .signUp(payload.email, payload.fullName, payload.authCode, payload.identityToken)
            .let(::AuthResponse)

    @PostMapping("/verify")
    suspend fun verify(@RequestBody payload: VerifyPayload) =
        authService
            .verify(payload.authCode, payload.identityToken)
            .let(::AuthResponse)

    data class AuthResponse(val accessToken: String)
    data class SignUpPayload(val email: String, val fullName: String, val authCode: String, val identityToken: String)
    data class VerifyPayload(val authCode: String, val identityToken: String)
}
