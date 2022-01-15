package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.domain.Offer
import com.zwolsman.bombastic.domain.PayOutOffer
import com.zwolsman.bombastic.domain.PointOffer
import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.helpers.validate
import com.zwolsman.bombastic.repositories.ProfileRepository
import org.springframework.stereotype.Service

@Service
class ProfileService(private val repo: ProfileRepository) {
    suspend fun createAppleUser(
        name: String,
        email: String,
        appleUserId: String,
        appleRefreshToken: String,
        appleAccessToken: String
    ): Profile {
        val profile = when (val appleUserProfile = repo.findByAppleUserId(appleUserId)) {
            null -> Profile(
                id = null,
                points = 0,
                name = name,
                email = email,
                gamesPlayed = 0,
                pointsEarned = 0,
                appleUserId = appleUserId,
                appleRefreshToken = appleRefreshToken,
                appleAccessToken = appleAccessToken,
                balanceInEur = 0.0,
            )
            else -> appleUserProfile.copy(
                name = name,
                email = email,
                appleRefreshToken = appleRefreshToken,
                appleAccessToken = appleAccessToken,
            )
        }

        return repo
            .save(profile)
    }

    suspend fun findByAppleUserId(appleUserId: String): Profile =
        repo
            .findByAppleUserId(appleUserId) ?: throw Exception("Profile not found")

    suspend fun findById(id: String): Profile =
        repo.findById(id) ?: throw Exception("Profile not found")

    suspend fun modifyPoints(profile: Profile, points: Int, earned: Int): Profile {
        validate(points >= 0) { IllegalArgumentException("Points should be >= 0") }
        validate(earned >= 0) { IllegalArgumentException("Earned should be >= 0") }

        val updatedProfile = profile.copy(
            points = profile.points + points,
            pointsEarned = profile.pointsEarned + earned,
        )
        return repo
            .save(updatedProfile)
    }

    suspend fun createGame(profile: Profile, initialBet: Int): Profile {
        val updatedProfile = profile.copy(
            points = profile.points - initialBet,
            gamesPlayed = profile.gamesPlayed + 1,
        )

        return repo
            .save(updatedProfile)
    }

    suspend fun redeemOffer(profile: Profile, offer: Offer): Profile {
        val updatedProfile = when (offer) {
            is PayOutOffer -> {
                validate(profile.points >= 0) { IllegalStateException("Not enough points to cash out") }
                profile.redeemOffer(offer)
            }
            is PointOffer -> {
                profile.redeemOffer(offer)
            }
        }

        return repo
            .save(updatedProfile)
    }

    private fun Profile.redeemOffer(offer: PayOutOffer) =
        copy(
            balanceInEur = balanceInEur + offer.reward,
            points = points - offer.price.toInt(),
        )

    private fun Profile.redeemOffer(offer: PointOffer) =
        copy(
            balanceInEur = balanceInEur - offer.price,
            points = points + offer.reward.toInt(),
        )
}
