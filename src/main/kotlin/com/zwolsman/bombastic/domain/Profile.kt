package com.zwolsman.bombastic.domain

import com.zwolsman.bombastic.db.ProfileModel

data class Profile(
    val id: String? = null,
    val points: Int,
    val name: String,
    val email: String,
    val gamesPlayed: Int,
    val pointsEarned: Int,
    val balanceInEur: Double,
    val address: String?,

    val appleUserId: String? = null,
    val appleRefreshToken: String? = null,
    val appleAccessToken: String? = null,
)

fun Profile(model: ProfileModel): Profile = Profile(
    id = model.id.toString(),
    points = model.points,
    name = model.name,
    email = model.email,
    gamesPlayed = model.gamesPlayed,
    pointsEarned = model.pointsEarned,
    balanceInEur = model.balanceInEur,
    address = model.address,
)
