package com.zwolsman.bombastic.controllers.auth.payload

data class VerifyPayload(val authCode: String, val identityToken: String)
