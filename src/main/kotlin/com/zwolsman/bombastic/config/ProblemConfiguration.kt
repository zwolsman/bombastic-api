package com.zwolsman.bombastic.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.server.WebExceptionHandler
import org.zalando.problem.jackson.ProblemModule
import org.zalando.problem.spring.webflux.advice.ProblemExceptionHandler
import org.zalando.problem.spring.webflux.advice.ProblemHandling
import org.zalando.problem.spring.webflux.advice.security.SecurityAdviceTrait
import org.zalando.problem.violations.ConstraintViolationProblemModule

@Configuration
class ProblemConfiguration {
    @ControllerAdvice
    internal class ExceptionHandling : ProblemHandling, SecurityAdviceTrait

    @Bean
    @Order(-2) // The handler must have precedence over WebFluxResponseStatusExceptionHandler and Spring Boot's ErrorWebExceptionHandler
    fun problemExceptionHandler(mapper: ObjectMapper, problemHandling: ProblemHandling): WebExceptionHandler =
        ProblemExceptionHandler(mapper, problemHandling)

    @Bean
    fun problemModule(): ProblemModule = ProblemModule()

    @Bean
    fun constraintViolationProblemModule(): ConstraintViolationProblemModule = ConstraintViolationProblemModule()
}
