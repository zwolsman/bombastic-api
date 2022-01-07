package com.zwolsman.bombastic.controllers.profile

import com.zwolsman.bombastic.services.ProfileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/v1/profiles")
class ProfileController(private val profileService: ProfileService) {

    @GetMapping("/me")
    suspend fun userProfile(principal: Principal): SimpleProfile {
        return byId(principal.name)
    }

    @GetMapping("/{id}")
    suspend fun byId(@PathVariable id: String): SimpleProfile {
        val profile = profileService
            .findById(id)

        return SimpleProfile(
            profile.name,
            profile.points,
            profile.gamesPlayed,
            profile.pointsEarned,
            "https://bombastic.io/u/${profile.id}"
        )
    }
}

data class SimpleProfile(
    val name: String,
    val points: Int,
    val games: Int,
    val totalEarnings: Int,
    val link: String,
)
