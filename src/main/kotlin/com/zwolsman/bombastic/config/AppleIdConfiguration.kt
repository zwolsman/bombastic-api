package com.zwolsman.bombastic.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "apple")
@ConstructorBinding
data class AppleIdConfiguration(val clientId: String, val teamId: String, val keyId: String)
