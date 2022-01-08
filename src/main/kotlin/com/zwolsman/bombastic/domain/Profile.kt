package com.zwolsman.bombastic.domain

import com.zwolsman.bombastic.db.ProfileModel

interface Profile {
    val id: String
    val points: Int
    val displayName: String
    val email: String
    val gamesPlayed: Int
    val pointsEarned: Int
    val balanceInEur: Double
}

fun Profile(model: ProfileModel): Profile = object : Profile {
    override val id = model.id.toString()
    override val points = model.points
    override val displayName = model.name
    override val email = model.email
    override val gamesPlayed = model.gamesPlayed
    override val pointsEarned = model.pointsEarned
    override val balanceInEur = model.balanceInEur
}