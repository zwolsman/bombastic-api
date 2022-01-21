package com.zwolsman.bombastic.db

import com.zwolsman.bombastic.domain.Profile
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("profiles")
data class ProfileModel(
    var points: Int,
    var name: String,
    var email: String,
    var gamesPlayed: Int,
    var pointsEarned: Int,
    var balanceInEur: Double,

    @Id
    var id: Long? = null,
    var appleUserId: String? = null,
    var appleRefreshToken: String? = null,
    var appleAccessToken: String? = null,
)

fun ProfileModel(profile: Profile) =
    ProfileModel(
        points = profile.points,
        name = profile.name,
        email = profile.email,
        gamesPlayed = profile.gamesPlayed,
        pointsEarned = profile.pointsEarned,
        balanceInEur = profile.balanceInEur,
        id = profile.id?.toLongOrNull(),
        appleUserId = profile.appleUserId,
        appleRefreshToken = profile.appleRefreshToken,
        appleAccessToken = profile.appleAccessToken,
    )
