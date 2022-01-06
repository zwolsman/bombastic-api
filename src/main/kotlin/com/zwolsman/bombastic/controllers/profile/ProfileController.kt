package com.zwolsman.bombastic.controllers.profile

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/v1/profiles")
class ProfileController {

    @GetMapping("/me")
    suspend fun userProfile(principal: Principal): SimpleProfile {
        return SimpleProfile(principal.name, 1000, 0, 0, "link")
    }

    @GetMapping("/{id}")
    fun byId(@PathVariable id: String): SimpleProfile {
        return SimpleProfile(id, 1000, 0, 0, "link")
    }
}

data class SimpleProfile(
    val name: String,
    val points: Int,
    val games: Int,
    val totalEarnings: Int,
    val link: String,
)
