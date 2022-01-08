package com.zwolsman.bombastic.security

import com.zwolsman.bombastic.services.AuthService
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthenticationManager(private val authService: AuthService) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val token = authentication.credentials.toString()
        val claims = authService.claims(token)

        return Mono.just(UsernamePasswordAuthenticationToken(claims.subject, null, emptyList()))
    }
}
