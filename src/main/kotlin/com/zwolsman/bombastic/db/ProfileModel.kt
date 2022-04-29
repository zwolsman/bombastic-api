package com.zwolsman.bombastic.db

import com.zwolsman.bombastic.domain.Profile
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("profiles")
data class ProfileModel(
    var bits: Long,
    var name: String,
    var email: String,
    var gamesPlayed: Int,
    var bitsEarned: Long,
    var address: String?,

    @Id
    var id: Long? = null,
    var appleUserId: String? = null,
    var appleRefreshToken: String? = null,
    var appleAccessToken: String? = null,
)

fun ProfileModel(profile: Profile) =
    ProfileModel(
        bits = profile.bits,
        name = profile.name,
        email = profile.email,
        gamesPlayed = profile.gamesPlayed,
        bitsEarned = profile.bitsEarned,
        id = profile.id?.toLongOrNull(),
        appleUserId = profile.appleUserId,
        appleRefreshToken = profile.appleRefreshToken,
        appleAccessToken = profile.appleAccessToken,
        address = profile.address,
    )
