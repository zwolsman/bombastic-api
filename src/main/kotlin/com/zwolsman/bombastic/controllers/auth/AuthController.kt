package com.zwolsman.bombastic.controllers.auth

import com.zwolsman.bombastic.controllers.auth.payload.SignUpPayload
import com.zwolsman.bombastic.controllers.auth.payload.VerifyPayload
import com.zwolsman.bombastic.controllers.auth.response.AuthResponse
import com.zwolsman.bombastic.controllers.auth.response.KeyResponse
import com.zwolsman.bombastic.controllers.auth.response.KeysResponse
import com.zwolsman.bombastic.services.AuthService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/sign-up")
    suspend fun signUp(@RequestBody payload: SignUpPayload): AuthResponse =
        authService
            .signUp(payload.email, payload.fullName, payload.authCode, payload.identityToken)
            .let(::AuthResponse)

    @PostMapping("/verify")
    suspend fun verify(@RequestBody payload: VerifyPayload): AuthResponse =
        authService
            .verify(payload.authCode, payload.identityToken)
            .let(::AuthResponse)

    @GetMapping("/keys")
    fun keys(): KeysResponse = authService
        .jsonWebKeys
        .map(::KeyResponse)
        .let(::KeysResponse)
}
