package com.zwolsman.bombastic.controllers.profile

import com.zwolsman.bombastic.db.ProfileModel
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
    suspend fun userProfile(principal: Principal): ProfileResponse {
        return byId(principal.name)
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

fun ProfileResponse(model: ProfileModel) = ProfileResponse(
    model.name,
    model.points,
    model.gamesPlayed,
    model.pointsEarned,
    "https://bombastic.io/u/${model.id}",
    model.balanceInEur,
)
