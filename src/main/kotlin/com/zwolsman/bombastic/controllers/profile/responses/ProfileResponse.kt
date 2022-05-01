package com.zwolsman.bombastic.controllers.profile.responses

import com.zwolsman.bombastic.domain.Profile

data class ProfileResponse(
    val name: String,
    val bits: Long,
    val games: Int,
    val bitsEarned: Long,
    val link: String,
    val address: String?,
)

fun ProfileResponse(profile: Profile, exposeAddress: Boolean = true) = ProfileResponse(
    profile.name,
    profile.bits,
    profile.gamesPlayed,
    profile.bitsEarned,
    "https://bombastic.joell.dev/api/v1/profiles/${profile.id}",
    profile.address.takeIf { exposeAddress },
)
