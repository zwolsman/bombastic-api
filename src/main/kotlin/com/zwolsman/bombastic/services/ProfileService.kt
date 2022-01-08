package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.controllers.store.Offer
import com.zwolsman.bombastic.db.ProfileModel
import com.zwolsman.bombastic.repositories.ProfileRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class ProfileService(private val repo: ProfileRepository) {
    suspend fun createAppleUser(
        name: String,
        email: String,
        appleUserId: String,
        appleRefreshToken: String,
        appleAccessToken: String
    ): ProfileModel {
        val profile = ProfileModel(
            points = 1000,
            name = name,
            email = email,
            gamesPlayed = 0,
            pointsEarned = 0,
            appleUserId = appleUserId,
            appleRefreshToken = appleRefreshToken,
            appleAccessToken = appleAccessToken,
            balanceInEur = 10.0,
        )

        return repo
            .save(profile)
            .awaitSingle()
    }

    suspend fun findByAppleUserId(appleUserId: String): ProfileModel {
        return repo
            .findByAppleUserId(appleUserId)
            .awaitSingle()
    }

    suspend fun findById(id: String): ProfileModel {
        return repo
            .findById(id.toLong())
            .awaitSingle()
    }

    suspend fun modifyPoints(id: String, points: Int): ProfileModel {
        val model = findById(id)
        model.points += points

        return repo
            .save(model)
            .awaitSingle()
    }

    suspend fun redeemOffer(id: String, offer: Offer): ProfileModel {
        val model = findById(id)
        model.points += offer.points
        model.balanceInEur -= offer.price

        require(model.points >= 0)
        return repo
            .save(model)
            .awaitSingle()
    }
}
