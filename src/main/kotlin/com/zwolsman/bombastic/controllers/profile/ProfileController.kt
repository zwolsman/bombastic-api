package com.zwolsman.bombastic.controllers.profile

import com.zwolsman.bombastic.controllers.profile.responses.ProfileResponse
import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.services.ProfileService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/profiles")
class ProfileController(private val profileService: ProfileService) {

    @GetMapping("/me")
    suspend fun userProfile(@AuthenticationPrincipal profile: Profile): ProfileResponse =
        profile
            .let(::ProfileResponse)

    @GetMapping("/{id}")
    suspend fun byId(@PathVariable id: String): ProfileResponse =
        profileService
            .findById(id)
            .let(::ProfileResponse)
}
