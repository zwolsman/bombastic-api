package com.zwolsman.bombastic.controllers.profile.responses

import com.zwolsman.bombastic.domain.Profile

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
