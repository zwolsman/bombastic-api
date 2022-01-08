package com.zwolsman.bombastic.domain

import com.zwolsman.bombastic.db.ProfileModel

data class Profile(
    val id: String,
    val points: Int,
    val name: String,
    val email: String,
    val gamesPlayed: Int,
    val pointsEarned: Int,
    val balanceInEur: Double,
)

fun Profile(model: ProfileModel): Profile = Profile(
    id = model.id.toString(),
    points = model.points,
    name = model.name,
    email = model.email,
    gamesPlayed = model.gamesPlayed,
    pointsEarned = model.pointsEarned,
    balanceInEur = model.balanceInEur,
)