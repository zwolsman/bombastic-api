package com.zwolsman.bombastic.controllers.profile

import com.zwolsman.bombastic.services.AppleIdService
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Base64

@RestController
@RequestMapping("/v1/profile")
class ProfileController(private val appleIdService: AppleIdService) {

    @GetMapping
    fun verify(@RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String, @RequestParam authCode: String) {
        val (_, identityToken) = authHeader.split(" ")
        Base64.getDecoder().decode(identityToken)
        appleIdService.verify(identityToken, authCode)
    }
}