package com.zwolsman.bombastic.repositories.appleId

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.zwolsman.bombastic.config.AppleIdConfiguration
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.context.annotation.Configuration as SpringConfiguration

@Repository
class AppleIdRepository(
    private val appleIdWebClient: WebClient,
    private val appleConfiguration: AppleIdConfiguration
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @SpringConfiguration
    class Configuration(objectMapper: ObjectMapper) {
        private val snakeCaseObjectMapper = objectMapper.copy().apply {
            propertyNamingStrategy =
                PropertyNamingStrategies.SNAKE_CASE
        }

        private val strategies = ExchangeStrategies
            .builder()
            .codecs { clientDefaultCodecsConfigurer: ClientCodecConfigurer ->
                clientDefaultCodecsConfigurer.defaultCodecs()
                    .jackson2JsonDecoder(Jackson2JsonDecoder(snakeCaseObjectMapper, MediaType.APPLICATION_JSON))
            }.build()

        @Bean
        fun appleIdWebClient() = WebClient
            .builder()
            .baseUrl("https://appleid.apple.com")
            .exchangeStrategies(strategies)
            .build()
    }

    suspend fun validateCode(secret: String, authCode: String): TokenResponse {
        val formBody = BodyInserters
            .fromFormData("client_id", appleConfiguration.clientId)
            .with("client_secret", secret)
            .with("code", authCode)
            .with("grant_type", "authorization_code")

        return try {
            val response = appleIdWebClient
                .post()
                .uri("/auth/token")
                .header("Accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(formBody)
                .retrieve()

            response.awaitBody()
        } catch (ex: WebClientResponseException) {
            log.error("error validating code, response: ${ex.responseBodyAsString}")
            throw ex
        }
    }
}
