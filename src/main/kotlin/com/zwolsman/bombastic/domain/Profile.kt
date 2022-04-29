package com.zwolsman.bombastic.domain

import com.zwolsman.bombastic.db.ProfileModel

data class Profile(
    val id: String? = null,
    val bits: Long,
    val name: String,
    val email: String,
    val gamesPlayed: Int,
    val bitsEarned: Long,
    val address: String?,

    val appleUserId: String? = null,
    val appleRefreshToken: String? = null,
    val appleAccessToken: String? = null,
)

fun Profile(model: ProfileModel): Profile = Profile(
    id = model.id.toString(),
    bits = model.bits,
    name = model.name,
    email = model.email,
    gamesPlayed = model.gamesPlayed,
    bitsEarned = model.bitsEarned,
    address = model.address,

    appleUserId = model.appleUserId,
    appleRefreshToken = model.appleRefreshToken,
    appleAccessToken = model.appleAccessToken,
)
