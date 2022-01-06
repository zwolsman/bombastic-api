package com.zwolsman.bombastic.security

import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val authenticationManager: ReactiveAuthenticationManager,
    private val securityContextRepository: ServerSecurityContextRepository
) {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    fun apiHttpSecurity(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .exceptionHandling()
            .authenticationEntryPoint { exchange, _ ->
                Mono.fromRunnable {
                    exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                }
            }.accessDeniedHandler { exchange, _ ->
                Mono.fromRunnable {
                    exchange.response.statusCode = HttpStatus.FORBIDDEN
                }
            }.and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .pathMatchers("/api/v1/auth/sign-up", "/api/v1/auth/verify").permitAll()
            .pathMatchers("/api/**").authenticated()
            .anyExchange().permitAll()
            .and().build()
    }

    @Bean
    fun userDetailsService() = ReactiveUserDetailsService { username ->
        Mono.just(User(username, null, emptyList()))
    }

    @Bean
    fun corsFilter(): CorsWebFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return CorsWebFilter(source)
    }
}

