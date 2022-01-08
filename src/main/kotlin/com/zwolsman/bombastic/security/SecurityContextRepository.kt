package com.zwolsman.bombastic.security

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecurityContextRepository(private val manager: ReactiveAuthenticationManager) : ServerSecurityContextRepository {
    companion object {
        private const val headerPrefix = "Bearer "
    }

    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> {
        TODO("Not yet implemented")
    }

    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> = Mono.justOrEmpty(
        exchange.request.headers.getFirst(
            HttpHeaders.AUTHORIZATION
        )
    )
        .filter { it.startsWith(headerPrefix) }
        .flatMap { authHeader ->
            val token = authHeader.substringAfter(headerPrefix)
            val auth = UsernamePasswordAuthenticationToken(token, token)
            manager.authenticate(auth).map(::SecurityContextImpl)
        }
}
