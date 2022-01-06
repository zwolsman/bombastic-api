package com.zwolsman.bombastic.db

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("profiles")
data class ProfileModel(
    var points: Int,
    var name: String,
    var email: String,
    var gamesPlayed: Int,
    var pointsEarned: Int,

    @Id
    var id: Long? = null,
    var appleUserId: String? = null,
    var appleRefreshToken: String? = null,
    var appleAccessToken: String? = null,
)
