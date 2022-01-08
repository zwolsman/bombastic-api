package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.db.ProfileModel
import com.zwolsman.bombastic.domain.Offer
import com.zwolsman.bombastic.domain.PayOutOffer
import com.zwolsman.bombastic.domain.PointOffer
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
            balanceInEur = 0.0,
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

    suspend fun modifyPoints(id: String, points: Int, earned: Int): ProfileModel {
        require(points >= 0)
        require(earned >= 0)

        val model = findById(id)
        model.points += points
        model.pointsEarned += earned

        return repo
            .save(model)
            .awaitSingle()
    }

    suspend fun createGame(id: String, initialBet: Int): ProfileModel {
        val model = findById(id)
        model.points -= initialBet
        model.gamesPlayed += 1

        return repo
            .save(model)
            .awaitSingle()
    }

    suspend fun redeemOffer(id: String, offer: Offer): ProfileModel {
        val model = findById(id)

        when (offer) {
            is PayOutOffer -> {
                model.balanceInEur += offer.reward
                model.points -= offer.price.toInt()
                require(model.points >= 0) { "Not enough points to cash out" }
            }
            is PointOffer -> {
                model.balanceInEur -= offer.price
                model.points += offer.reward.toInt()
            }
        }

        return repo
            .save(model)
            .awaitSingle()
    }
}
