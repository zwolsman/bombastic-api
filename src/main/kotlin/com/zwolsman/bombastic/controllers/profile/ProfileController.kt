package com.zwolsman.bombastic.controllers.profile

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
    suspend fun userProfile(@AuthenticationPrincipal profile: Profile): ProfileResponse {
        return profile
            .let(::ProfileResponse)
    }

    @GetMapping("/{id}")
    suspend fun byId(@PathVariable id: String): ProfileResponse {
        return profileService
            .findById(id)
            .let(::ProfileResponse)
    }
}

data class ProfileResponse(
    val name: String,
    val points: Int,
    val games: Int,
    val totalEarnings: Int,
    val link: String,
    val balanceInEur: Double,
)

fun ProfileResponse(profile: Profile) = ProfileResponse(
    profile.name,
    profile.points,
    profile.gamesPlayed,
    profile.pointsEarned,
    "https://bombastic.io/u/${profile.id}",
    profile.balanceInEur,
)
