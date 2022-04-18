package com.zwolsman.bombastic.controllers.profile.responses

import com.zwolsman.bombastic.domain.Profile

data class ProfileResponse(
    val name: String,
    val bits: Long,
    val games: Int,
    val bitsEarned: Long,
    val link: String,
    val address: String,
)

fun ProfileResponse(profile: Profile) = ProfileResponse(
    profile.name,
    profile.bits,
    profile.gamesPlayed,
    profile.bitsEarned,
    "https://bombastic.io/u/${profile.id}",
    profile.address,
)
