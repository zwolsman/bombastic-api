package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.db.ProfileModel
import com.zwolsman.bombastic.domain.Offer
import com.zwolsman.bombastic.domain.PayOutOffer
import com.zwolsman.bombastic.domain.PointOffer
import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.repositories.ProfileRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
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
        val model = when (val registeredModel = repo.findByAppleUserId(appleUserId).awaitSingleOrNull()) {
            null -> ProfileModel(
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
            else -> registeredModel.copy(
                name = name,
                email = email,
                appleRefreshToken = appleRefreshToken,
                appleAccessToken = appleAccessToken,
            )
        }

        return repo
            .save(model)
            .awaitSingle()
            .let(::Profile)
    }

    suspend fun findByAppleUserId(appleUserId: String): Profile {
        return repo
            .findByAppleUserId(appleUserId)
            .awaitSingle()
            .let(::Profile)
    }

    suspend fun findById(id: String): Profile {
        return findModelById(id)
            .let(::Profile)
    }

    private suspend fun findModelById(id: String): ProfileModel {
        return repo
            .findById(id.toLong())
            .awaitSingle()
    }

    suspend fun modifyPoints(id: String, points: Int, earned: Int): Profile {
        require(points >= 0)
        require(earned >= 0)

        val model = findModelById(id)
        model.points += points
        model.pointsEarned += earned

        return repo
            .save(model)
            .awaitSingle()
            .let(::Profile)
    }

    suspend fun createGame(id: String, initialBet: Int): Profile {
        val model = findModelById(id)
        model.points -= initialBet
        model.gamesPlayed += 1

        return repo
            .save(model)
            .awaitSingle()
            .let(::Profile)
    }

    suspend fun redeemOffer(id: String, offer: Offer): Profile {
        val model = findModelById(id)

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
            .let(::Profile)
    }
}
