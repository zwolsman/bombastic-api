package com.zwolsman.bombastic.controllers.auth.payload

data class SignUpPayload(val email: String, val fullName: String, val authCode: String, val identityToken: String)
