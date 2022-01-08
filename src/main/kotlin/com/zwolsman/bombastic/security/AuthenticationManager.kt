package com.zwolsman.bombastic.security

import com.zwolsman.bombastic.services.AuthService
import com.zwolsman.bombastic.services.ProfileService
import kotlinx.coroutines.runBlocking
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthenticationManager(private val authService: AuthService, private val profileService: ProfileService) :
    ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val token = authentication.credentials.toString()
        val claims = authService.claims(token)
        val profile = runBlocking { profileService.findById(claims.subject) } // TODO

        return Mono.just(AuthenticatedProfile(profile))
    }
}

